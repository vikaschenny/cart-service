/*
import java.util.Arrays
import java.util.UUID
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.cache.store.cassandra.CassandraCacheStore
import org.apache.ignite.cache.store.cassandra.CassandraCacheStoreFactory
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

object IgniteTest {
  def main(args: Array[String]) {
    val cacheId: String = "partitioned-priya8"
    //CassandraCacheStoreFactory<String, String> cachced = new CassandraCacheStoreFactory<>();
    val spi: TcpDiscoverySpi = new TcpDiscoverySpi
    //TcpDiscoveryMulticastIpFinder multicastIpFinder = new TcpDiscoveryMulticastIpFinder();
    //multicastIpFinder.setAddresses(Arrays.asList("54.211.236.81:47500"));
    val ipFinder: TcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder
    //TcpCommunicationSpi comSpi = new TcpCommunicationSpi();
    //comSpi.setLocalAddress("54.84.212.22");
    //comSpi.setLocalPort(47100);
    ipFinder.setAddresses(Arrays.asList("54.84.212.22:47500..47509"))
    //ipFinder.setAddresses(Arrays.asList("127.0.0.1"));
    //System.out.println("local addresses: " +spi.getLocalAddress());
    //spi.setLocalAddress("192.168.0.115");
    //spi.setLocalPort(47110);
    spi.setIpFinder(ipFinder)
    //spi.setLocalAddress("127.0.0.1");
    val cfg: IgniteConfiguration = new IgniteConfiguration
    //cfg.setNodeId(UUID.fromString("03210b8a-d381-4721-8c20-31dbc5a1a03e"));
    cfg.setDiscoverySpi(spi)
    //cfg.setCommunicationSpi(comSpi);
    // Required to fix "Cache mode mismatch" exception
    // Run local node as client only
    //cacheCfg.setDistributionMode(CacheDistributionMode.CLIENT_ONLY);
    //cfg.setCacheConfiguration(cacheCfg);
    Ignition.setClientMode(true)
    val ignite: Ignite = Ignition.start(cfg)
    val cacheCfg: CacheConfiguration[Integer, String] = new CacheConfiguration[Integer, String]
    cacheCfg.setName(cacheId)
    //cacheCfg.setCacheMode(CacheMode.PARTITIONED);
    val cache: IgniteCache[Integer, String] = ignite.getOrCreateCache(cacheCfg)
    // Store keys in cache (values will end up on different cache nodes).
    var i: Int = 0
    while (i < 10) {
      cache.put(i, Integer.toString(i)) {
        i += 1; i - 1
      }
    }
    var i: Int = 0
    while (i < 10) {
      System.out.println("Got [key=" + i + ", val=" + cache.get(i) + ']') {
        i += 1; i - 1
      }
    }
  }
}*/
