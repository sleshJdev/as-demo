-- Generated by protobuf; do not edit
local module = {}
local protobuf = require 'protobuf'

module.PERSON = protobuf.Descriptor()
module.PERSON_ID_FIELD = protobuf.FieldDescriptor()
module.PERSON_NAME_FIELD = protobuf.FieldDescriptor()
module.PERSON_EMAIL_FIELD = protobuf.FieldDescriptor()

module.PERSON_ID_FIELD.name = 'id'
module.PERSON_ID_FIELD.full_name = '.persons.Person.id'
module.PERSON_ID_FIELD.number = 1
module.PERSON_ID_FIELD.index = 0
module.PERSON_ID_FIELD.label = 1
module.PERSON_ID_FIELD.has_default_value = false
module.PERSON_ID_FIELD.default_value = 0
module.PERSON_ID_FIELD.type = 5
module.PERSON_ID_FIELD.cpp_type = 1

module.PERSON_NAME_FIELD.name = 'name'
module.PERSON_NAME_FIELD.full_name = '.persons.Person.name'
module.PERSON_NAME_FIELD.number = 2
module.PERSON_NAME_FIELD.index = 1
module.PERSON_NAME_FIELD.label = 1
module.PERSON_NAME_FIELD.has_default_value = false
module.PERSON_NAME_FIELD.default_value = ''
module.PERSON_NAME_FIELD.type = 9
module.PERSON_NAME_FIELD.cpp_type = 9

module.PERSON_EMAIL_FIELD.name = 'email'
module.PERSON_EMAIL_FIELD.full_name = '.persons.Person.email'
module.PERSON_EMAIL_FIELD.number = 3
module.PERSON_EMAIL_FIELD.index = 2
module.PERSON_EMAIL_FIELD.label = 1
module.PERSON_EMAIL_FIELD.has_default_value = false
module.PERSON_EMAIL_FIELD.default_value = ''
module.PERSON_EMAIL_FIELD.type = 9
module.PERSON_EMAIL_FIELD.cpp_type = 9

module.PERSON.name = 'Person'
module.PERSON.full_name = '.persons.Person'
module.PERSON.nested_types = {}
module.PERSON.enum_types = {}
module.PERSON.fields = {module.PERSON_ID_FIELD, module.PERSON_NAME_FIELD, module.PERSON_EMAIL_FIELD}
module.PERSON.is_extendable = false
module.PERSON.extensions = {}

module.Person = protobuf.Message(module.PERSON)


module.MESSAGE_TYPES = {'Person'}
module.ENUM_TYPES = {}

return module
