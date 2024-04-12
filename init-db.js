const dbName = 'postsdb';
const documents = [
  { _id: 'posts', seq: 100 }
];

const conn = new Mongo();
const db = conn.getDB(dbName);

const collection = db.getCollection('sequences');
collection.insertMany(documents);