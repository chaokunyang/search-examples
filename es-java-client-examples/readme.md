# 1、Embedding jar with dependencies
If you want to create a single jar containing your application and all dependencies, you should not use maven-assembly-plugin for that because it can not deal with META-INF/services structure which is required by Lucene jars.

Instead, you can use maven-shade-plugin and configure it as follow:

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.4.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```
Note that if you have a main class you want to automatically call when running java -jar yourjar.jar, just add it to the transformers:

```
<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
    <mainClass>org.elasticsearch.demo.Generate</mainClass>
</transformer>
```

# 2、cluster sniffing feature 
The Transport client comes with a cluster sniffing feature which allows it to dynamically add new hosts and remove old ones. When sniffing is enabled, the transport client will connect to the nodes in its internal node list, which is built via calls to addTransportAddress. After this, the client will call the internal cluster state API on those nodes to discover available data nodes. The internal node list of the client will be replaced with those data nodes only. This list is refreshed every five seconds by default. Note that the IP addresses the sniffer connects to are the ones declared as the publish address in those node’s elasticsearch config.

Keep in mind that the list might possibly not include the original node it connected to if that node is not a data node. If, for instance, you initially connect to a master node, after sniffing, no further requests will go to that master node, but rather to any data nodes instead. The reason the transport client excludes non-data nodes is to avoid sending search traffic to master only nodes.