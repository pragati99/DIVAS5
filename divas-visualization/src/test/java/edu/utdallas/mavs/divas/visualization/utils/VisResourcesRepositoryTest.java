package edu.utdallas.mavs.divas.visualization.utils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for VisResourcesRepository
 */
public class VisResourcesRepositoryTest
{
    /**
     * Tests find all objects
     */
    @Ignore
    @Test
    public void TestFindAllObjects()
    {
        int expected = EnvObjectLoader.getVisResources().size();
        int actual = VisResourcesRepository.findAllEnvObjects().size();
        Assert.assertEquals(expected, actual);
    }
}
