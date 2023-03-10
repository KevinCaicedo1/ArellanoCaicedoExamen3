print('############################ScriptStart####################################');

db = db.getSiblingDB('storage');
db.createCollection('branches');

var branchColl = db.getCollection('branches');

branchColl.insertMany([{
    "code": "UIO",
    "name": "QUITO",
  },
  {
    "code": "AMB",
    "name": "AMBATO",
  }]);

print('############################ScriptEnd######################################');

