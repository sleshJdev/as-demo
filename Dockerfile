FROM aerospike/aerospike-server:3.13.0.11
RUN apt-get update \
    && apt-get install -y python3 python3-pip luarocks git \
    && ln -sf /usr/bin/python3 /usr/bin/python \
    && pip3 install protobuf \
    && luarocks install protobuf 1.1.2-0 \
    && ln -s /usr/local/lib/luarocks/rocks/protobuf/1.1.2-0/protoc-plugin/protoc-gen-lua /usr/local/bin/  \
    && wget "https://github.com/protocolbuffers/protobuf/releases/download/v3.15.8/protoc-3.15.8-linux-x86_64.zip" \
    && unzip protoc-3.15.8-linux-x86_64.zip -d /usr/local \
    && rm -f protoc-3.15.8-linux-x86_64.zip
RUN mkdir -p /lua && mkdir -p proto && mkdir -p udf
ADD ./src/main/proto /proto
ADD ./src/main/resources/udf /udf
RUN protoc -I/proto/ --lua_out=/lua/ person.proto
