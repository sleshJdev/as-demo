package org.example;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.query.Statement;
import com.playtika.test.aerospike.AerospikeProperties;
import com.playtika.test.aerospike.AerospikeWaitStrategy;
import lombok.extern.slf4j.Slf4j;
import net.aerospike.demo.PersonProto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.StreamSupport;

import static com.playtika.test.common.utils.ContainerUtils.configureCommonsAndStart;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@Slf4j
@Testcontainers
public class AppTest {
    private static final String SET_NAME = "foo";
    final AerospikeProperties props = aerospikeProps();
    @Container
    public final GenericContainer<?> aerospike = aerospike(props);
    private AerospikeClient client;

    @BeforeEach
    void setUp() {
        client = new AerospikeClient(aerospike.getHost(), aerospike.getFirstMappedPort());
        final var person1 = PersonProto.Person.newBuilder()
                .setId(1).setName("bob").setEmail("bob@mail.com").build();
        final var key1 = new Key(props.getNamespace(), SET_NAME, "person1");
        client.put(null, key1, new Bin("person", person1.toByteArray()));

        final var person2 = PersonProto.Person.newBuilder()
                .setId(2).setName("alisa").setEmail("alisa@mail.com").build();
        final var key2 = new Key(props.getNamespace(), SET_NAME, "person2");
        client.put(null, key2, new Bin("person", person2.toByteArray()));
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void testAqlIsAbleToCallLuaFuncToConvertDecodeProtoDigestAndReturnJson() throws Exception {
        final var exec1 = aerospike.execInContainer("sh", "-c", "aql -c 'register module \"/lua/person_pb.lua\"'");
        assertThat(exec1.getStdout(), containsString("OK, 1 module added"));
        final var exec2 = aerospike.execInContainer("sh", "-c", "aql -c 'register module \"/udf/demo.lua\"'");
        assertThat(exec2.getStdout(), containsString("OK, 1 module added"));
        final var exec3 = aerospike.execInContainer("sh", "-c", "aql -o json -c \"execute demo.decode() on TEST.foo where PK='person1'\"");
        assertThat(exec3.getStdout(), containsString("[\n" +
                "  {\n    \"decode\": {\n" +
                "      \"name\": \"bob\",\n" +
                "      \"id\": 1,\n" +
                "      \"email\": \"bob@mail.com\"\n" +
                "    }\n  }\n]\n\n\n"));
    }

    @Test
    @Disabled
    void testJavaAerospikeClient() {
//        it will fail even if comment out those lines which register modules on AS side
//        final var exec1 = aerospike.execInContainer("sh", "-c", "aql -c 'register module \"/lua/person_pb.lua\"'");
//        assertThat(exec1.getStdout(), containsString("OK, 1 module added"));
//        final var exec2 = aerospike.execInContainer("sh", "-c", "aql -c 'register module \"/udf/demo.lua\"'");
//        assertThat(exec2.getStdout(), containsString("OK, 1 module added"));

        final var statement = new Statement();
        statement.setNamespace(props.getNamespace());
        statement.setSetName(SET_NAME);
        statement.setAggregateFunction(
                Thread.currentThread().getContextClassLoader(),
                "udf/demo.lua", "demo", "foo");
        try (final var rs = client.queryAggregate(null, statement)) {
            StreamSupport.stream(rs.spliterator(), false).forEach(System.out::println);
        }
        
        /* how to fix it?
            com.aerospike.client.AerospikeException: Error -1: org.luaj.vm2.LuaError: demo:1 module 'person_pb' not found: person_pb
                no field package.preload['person_pb']
                person_pb.lua
                no class 'person_pb'
            stack traceback:
                demo:1: in main chunk
                [Java]: in ? 
        * */
    }

    private AerospikeProperties aerospikeProps() {
        final var props = new AerospikeProperties();
        props.setDockerImage("as:demo");
        return props;
    }

    private GenericContainer<?> aerospike(AerospikeProperties props) {
        log.info("Starting aerospike server. Docker image: {}", props.getDockerImage());
        WaitStrategy waitStrategy = new WaitAllStrategy()
                .withStrategy(new AerospikeWaitStrategy(this.props))
                .withStrategy(new HostPortWaitStrategy())
                .withStartupTimeout(props.getTimeoutDuration());

        GenericContainer<?> aerospike =
                new GenericContainer<>(DockerImageName.parse(props.getDockerImage()))
                        .withExposedPorts(props.getPort())
                        // see https://github.com/aerospike/aerospike-server.docker/blob/master/aerospike.template.conf
                        .withEnv("NAMESPACE", props.getNamespace())
                        .withEnv("SERVICE_PORT", String.valueOf(props.getPort()))
                        .withEnv("MEM_GB", String.valueOf(1))
                        .withEnv("STORAGE_GB", String.valueOf(1))
                        .waitingFor(waitStrategy);
        String featureKey = props.getFeatureKey();
        if (featureKey != null) {
            // see https://github.com/aerospike/aerospike-server-enterprise.docker/blob/master/aerospike.template.conf
            aerospike
                    .withEnv("FEATURES", featureKey)
                    .withEnv("FEATURE_KEY_FILE", "env-b64:FEATURES");
        }
        return configureCommonsAndStart(aerospike, props, log);
    }
}

