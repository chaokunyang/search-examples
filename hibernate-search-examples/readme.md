# Hibernate Search 
## database config
```
DROP DATABASE h_search_demo;
CREATE DATABASE h_search_demo DEFAULT CHARACTER SET 'utf8' DEFAULT COLLATE 'utf8_unicode_ci';
USE h_search;
grant all privileges on *.* to 'h_search'@'localhost' identified by '123456'; 
grant all privileges on *.* to 'h_search'@'127.0.0.1' identified by '123456'; 
grant all privileges on *.* to 'h_search'@'::1' identified by '123456'; 
flush privileges;
```


## Enable full-text search capabilities on your Entities
To achieve this you have to add a few annotations to the Book and Author class:

### Define which entities need to be indexed

The annotation @Indexed marks Book as an entity which needs to be indexed by Hibernate Search.

### Pick a unique identifier

Hibernate Search needs to store the entity identifier in the index for each entity. By default, it will use for this purpose the field marked with @Id but you can override this using @DocumentId (advanced users only).

Choose what to index, and how

Next you have to mark the fields you want to make searchable. Let’s start with title and subtitle and annotate both with @Field.

The parameter index=Index.YES will ensure that the text will be indexed, while analyze=Analyze.YES ensures that the text will be analyzed using the default Lucene analyzer.

Analyzer options are important concept that we will better explain in the reference documentation. For the purpose of a simple introduction, let’s simplify and say that analyzing means chunking a sentence into individual words, lowercase them and potentially excluding common words like 'a' or 'the'.

Store option and Projections

The third parameter, store=Store.NO, ensures that the actual data will not be stored in the index. Whether this data is stored in the index or not has nothing to do with the ability to search for it: the benefit of storing it is the ability to retrieve it via projections (see Projections).

When not using projections Hibernate Search will execute a Lucene query in order to find the database identifiers of the entities matching the query and use these identifiers to retrieve managed objects from the database. If you use projections you might avoid the roundtrip to the database, but this will only return object arrays and not the managed objects you get from a normal query.

Note that index=Index.YES, analyze=Analyze.YES and store=Store.NO are the default values for these parameters and could be omitted.

Some types might need encoding

The Lucene index is mostly string based, with some additional support for numeric types. For this reason Hibernate Search must convert the data types of the indexed fields to strings and vice versa. The exception are those properties like Integer, Long, Calendar.. these are all indexed as NumericField which means they will be encoded in a representation more suited for range queries.

Many predefined bridges are provided, for example the BooleanBridge will encode properties of type Boolean to literals "true" or "false"; by so doing they are searchable by keyword.

In the case of our example, the Book entity has a Date property so if we want to make this property searchable too, we will need to annotate it with both @Field and @DateBridge.

For more details see Bridges.

Indexing of associated entities

The @IndexedEmbedded annotation is used to index associated entities, like those normally defined via @ManyToMany, @OneToOne, @ManyToOne, @Embedded and @ElementCollection.

Note however that the properties of the associated entities are embedded in the same index entry of the entity being marked with @Indexed, essentially denormalizing the data. This is needed since a Lucene index document is a flat data structure which is not suited to store relational information.

In our example, to ensure that the author’s name will be searchable you have to make sure that the names are indexed as part of the book itself. On top of @IndexedEmbedded you will also have to mark all fields of the associated entity you want to have included in the index with @Indexed. For more details see Embedded and Associated Objects.

## lucene index 
catalog: C:\chaokunyang\Devlopment\DevEnvironment\apache-tomcat-8.0.22-windows-x64\apache-tomcat-8.0.22\bin\lucene\indexes

## initial index
```
FullTextSession fullTextSession = Search.getFullTextSession(session);
fullTextSession.createIndexer().startAndWait();
```
```
FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
fullTextEntityManager.createIndexer().startAndWait();
```

## Searching

