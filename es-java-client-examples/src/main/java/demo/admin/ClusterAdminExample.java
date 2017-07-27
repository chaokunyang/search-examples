package demo.admin;

import demo.ClientExecutor;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.common.unit.TimeValue;

/**
 * @author chaokunyang
 */
public class ClusterAdminExample {

    public static void main(String[] args) {
        ClientExecutor.execute(ClusterAdminExample::clusterAdmin);
    }

    private static void clusterAdmin(Client client) {

        ClusterHealthResponse healths = client.admin().cluster().prepareHealth().get();
        String clusterName = healths.getClusterName();
        int numberOfDataNodes = healths.getNumberOfDataNodes();
        int numberOfNodes = healths.getNumberOfNodes();
        System.out.printf("clusterName %s, numberOfDataNodes %s, numberOfNodes %s", clusterName, numberOfDataNodes, numberOfNodes);
        System.out.println();

        for (ClusterIndexHealth indexHealth : healths.getIndices().values()) {
            String index = indexHealth.getIndex();
            int numberOfShards = indexHealth.getNumberOfShards();
            int numberOfReplicas = indexHealth.getNumberOfReplicas();
            ClusterHealthStatus status = indexHealth.getStatus();
            System.out.printf("index %s, numberOfShards %s, numberOfReplicas %s, status %s", index, numberOfShards, numberOfReplicas, status);
            System.out.println();
        }


        // You can use the cluster health API to wait for a specific status for the whole cluster or for a given index:
        client.admin().cluster().prepareHealth()
                .setWaitForYellowStatus() // 大于等于Yellow即可，比如调用之前就是green状态
                .get();
        client.admin().cluster().prepareHealth("company")
                .setWaitForGreenStatus()
                .get();

        client.admin().cluster().prepareHealth("twitter2")
                .setWaitForGreenStatus()
                .setTimeout(TimeValue.timeValueSeconds(2))
                .get();


        // If the index does not have the expected status and you want to fail in that case, you need to explicitly interpret the result:
        ClusterHealthResponse response = client.admin().cluster().prepareHealth("company")
                .setWaitForGreenStatus()
                .get();

        ClusterHealthStatus status = response.getIndices().get("company").getStatus();
        if (!status.equals(ClusterHealthStatus.GREEN)) {
            throw new RuntimeException("Index is in " + status + " state");
        }
    }

}
