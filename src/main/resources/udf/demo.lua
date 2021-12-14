local person_pb = require("person_pb")

function decode(rec)
    local pb_bytes = rec["person"]
    local pb_data = bytes.get_string(pb_bytes, 1, bytes.size(pb_bytes))
    local msg = person_pb.Person()
    msg:ParseFromString(pb_data)
    local result = map()
    result["id"] = msg.id
    result["name"] = msg.name
    result["email"] = msg.email
    return result
end