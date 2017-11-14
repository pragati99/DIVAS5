//package edu.utdallas.mavs.divas.core.sim.agent;
//
//import java.io.Serializable;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import edu.utdallas.mavs.divas.core.config.BaseConfig;
//import edu.utdallas.mavs.divas.core.config.ConfigKey;
//import edu.utdallas.mavs.divas.core.config.ConfigProperty;
//import edu.utdallas.mavs.divas.core.config.SimConfig;
//
//public class ConfigTest implements Serializable
//{
//    private final ConfigKey   key              = ConfigKey.create("test");
//    private final ConfigKey   key2             = ConfigKey.create("test2");
//    private static final long serialVersionUID = 1L;
//
//    @Test
//    public void testConfig()
//    {
//        TestConfig config = new TestConfig();
//        config = config.load();
//
//        config.customProperties.clear();
//        config.save();
//        config = config.load();
//
//        Assert.assertEquals(0, config.customProperties.size());
//
//        config.addCustomProperty(new ConfigProperty<Integer>(key, 153, "this is a number", true));
//        config.addCustomProperty(new ConfigProperty<Boolean>(key2, false, "this is a boolean", false));
//
//        Assert.assertEquals(2, config.customProperties.size());
//
//        config.save();
//        config = config.load();
//
//        Assert.assertEquals(2, config.customProperties.size());
//        Assert.assertEquals(153, config.getCustomProperty(key));
//        Assert.assertEquals(false, config.getCustomProperty(key2));
//
//        config.customProperties.clear();
//        config.save();
//        config = config.load();
//
//        Assert.assertEquals(0, config.customProperties.size());
//    }
//
//    @Test
//    public void testSimConfig()
//    {
//        SimConfig config = SimConfig.getInstance();
//
//        config.customProperties.clear();
//        Assert.assertEquals(0, config.customProperties.size());
//
//        config.addCustomProperty(new ConfigProperty<Integer>(key, 153, "this is a number", false));
//        config.addCustomProperty(new ConfigProperty<Boolean>(key2, false, "this is a boolean", false));
//
//        Assert.assertEquals(2, config.customProperties.size());
//
//        config.save();
//        SimConfig.refresh();
//        config = SimConfig.getInstance();
//
//        Assert.assertEquals(2, config.customProperties.size());
//        Assert.assertEquals(153, config.getCustomProperty(key));
//        Assert.assertEquals(false, config.getCustomProperty(key2));
//    }
//
//    class TestConfig extends BaseConfig
//    {
//        private static final long   serialVersionUID = 1L;
//        private static final String fileName         = "_test";
//
//        public void save()
//        {
//            save(fileName);
//        }
//
//        public TestConfig load()
//        {
//            TestConfig config = (TestConfig) load(fileName);
//            System.out.println(config);
//            if(config == null)
//            {
//                config = new TestConfig();
//            }
//            return config;
//        }
//    }
//}
