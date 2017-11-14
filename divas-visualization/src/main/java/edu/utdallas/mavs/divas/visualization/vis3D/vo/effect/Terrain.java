package edu.utdallas.mavs.divas.visualization.vis3D.vo.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;

/**
 * This class describes the terrain showed in the 3D visualization.
 */
public class Terrain
{

    Material                   mat;

    private static TerrainQuad terrain;
    static Material            matTerrain;
    private static int         xt;
    private static int         zt;
    private static float       dim;       // The dimension of the flat area
    private static float       alt;       // The height of the flat area
    private static int         terMargin; // The length of the margins of the
                                           // flat area

    private Terrain()
    {}

    private static void adjustSimulationArea(int terX, int terZ, float terDimension, float hieght)
    {
        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();
        // These two loops adjust the height of the square originated at point
        // (terX,terY) with length terDimension to the new height "height"
        // In other word it makes a square flat area in the terrain
        for(int x = terX; x <= terX + terDimension; x++)
        {
            for(int y = terZ; y <= terZ + terDimension; y++)
            {
                locs.add(new Vector2f(x, y));
                heights.add(hieght);
            }
        }

        float borderHieghtModLowerLeft = 0; // This represent the amount of
                                            // increase or decrease to the
                                            // height for any point in the
                                            // margin in the lower left corner
                                            // of the terrain
        float borderHieghtModUpperLeft = 0; // This represent the amount of
                                            // increase or decrease to the
                                            // height for any point in the
                                            // margin in the upper left corner
                                            // of the terrain
        float borderHieghtModLowerRight = 0; // This represent the amount of
                                             // increase or decrease to the
                                             // height for any point in the
                                             // margin in the lower right corner
                                             // of the terrain
        float borderHieghtModUpperRight = 0; // This represent the amount of
                                             // increase or decrease to the
                                             // height for any point in the
                                             // margin in the upper right corner
                                             // of the terrain
        float borderHieghtMod = 0; // This represent the amount of increase or
                                   // decrease to the height for any point in
                                   // the margin
        float newHeight; // This variable represent the point in the margin area
                         // new height to make the margin look smooth compared
                         // to the flat area
        float heightParallelToBorder = 0; // This variable represent the height
                                          // of the point parallel to the flat
                                          // area of the terrain at margin
                                          // distance to make the margin area
                                          // look smooth
        float heightParallelToCorner = 0;// This variable represent the new
                                         // height of the point in the margin in
                                         // the corners of the terrain, created

        // Adjust the top margin area of the flat area to make it smooth
        for(int x = terZ; x < terZ + terDimension; x++)
        {

            heightParallelToBorder = 2 * terrain.getHeight(new Vector2f(terX - terMargin, x));
            borderHieghtMod = (hieght - heightParallelToBorder) / terMargin;
            if(x == terZ)
            {
                borderHieghtModLowerLeft = borderHieghtMod;
            }
            else if(x == terZ + terDimension - 1)
            {
                borderHieghtModLowerRight = borderHieghtMod;
            }

            int step = 0;
            for(float y = terX - terMargin; y < terX; y++)
            {
                newHeight = heightParallelToBorder + borderHieghtMod * (step);
                locs.add(new Vector2f(y, x));
                newHeight = (((newHeight >= heightParallelToBorder) && (newHeight < hieght)) || ((newHeight <= heightParallelToBorder) && (newHeight > hieght))) ? newHeight : heightParallelToBorder;
                heights.add(newHeight);
                step += 1;
            }
        }

        borderHieghtMod = 0;
        newHeight = 0;
        heightParallelToBorder = 0;
        heightParallelToCorner = 0;

        // Adjust the bottom margin area of the flat area to make it smooth
        for(int x = terZ; x < terZ + terDimension; x++)
        {
            heightParallelToBorder = 2 * terrain.getHeight(new Vector2f(terX + terDimension + terMargin, x));
            borderHieghtMod = (hieght - heightParallelToBorder) / terMargin;

            if(x == terZ)
            {
                borderHieghtModUpperLeft = borderHieghtMod;
            }
            else if(x == terZ + terDimension - 1)
            {
                borderHieghtModUpperRight = borderHieghtMod;
            }

            int step = 0;
            for(float y = terX + terDimension + terMargin; y > terX + terDimension; y--)
            {
                newHeight = heightParallelToBorder + borderHieghtMod * (step);
                locs.add(new Vector2f(y, x));
                newHeight = (((newHeight >= heightParallelToBorder) && (newHeight < hieght)) || ((newHeight <= heightParallelToBorder) && (newHeight > hieght))) ? newHeight : heightParallelToBorder;
                heights.add(newHeight);
                step += 1;
            }
        }

        borderHieghtMod = 0;
        newHeight = 0;
        heightParallelToBorder = 0;
        heightParallelToCorner = 0;

        // Adjust the left margin area of the flat area to make it smooth

        for(int x = terX - terMargin; x < terX + terDimension + terMargin; x++)
        {
            int step = 0;
            heightParallelToBorder = 2 * terrain.getHeight(new Vector2f(x, terZ - terMargin));

            if((x >= terX) && (x <= terX + terDimension))
            {
                borderHieghtMod = (hieght - heightParallelToBorder) / terMargin;
            }
            else if(x < terX)
            {
                heightParallelToCorner = hieght + (-1 * (borderHieghtModLowerLeft) * (terMargin - (x - (terX - terMargin))));
                borderHieghtMod = (heightParallelToCorner - heightParallelToBorder) / terMargin;
            }
            else if(x > terX + terDimension)
            {
                heightParallelToCorner = hieght + (-1 * (borderHieghtModUpperLeft) * (x - (terX + terDimension)));
                borderHieghtMod = (heightParallelToCorner - heightParallelToBorder) / terMargin;
            }

            for(float y = terZ - terMargin; y < terZ; y++)
            {
                newHeight = heightParallelToBorder + borderHieghtMod * (step);
                locs.add(new Vector2f(x, y));
                if((x >= terX) && (x <= terX + terDimension))
                {
                    newHeight = (((newHeight >= heightParallelToBorder) && (newHeight < hieght)) || ((newHeight <= heightParallelToBorder) && (newHeight > hieght))) ? newHeight
                            : heightParallelToBorder;
                }

                heights.add(newHeight);
                step += 1;
            }
        }

        borderHieghtMod = 0;
        newHeight = 0;
        heightParallelToBorder = 0;
        heightParallelToCorner = 0;

        // Adjust the right margin area of the flat area to make it smooth
        for(int x = terX - terMargin; x < terX + terDimension + terMargin; x++)
        {
            heightParallelToBorder = 2 * terrain.getHeight(new Vector2f(x, terZ + terDimension + terMargin));
            if((x >= terX) && (x <= terX + terDimension))
            {
                borderHieghtMod = (hieght - heightParallelToBorder) / terMargin;
            }
            else if(x < terX)
            {
                heightParallelToCorner = hieght + (-1 * (borderHieghtModLowerRight) * (terMargin - (x - (terX - terMargin))));
                borderHieghtMod = (heightParallelToCorner - heightParallelToBorder) / terMargin;
            }
            else if(x > terX + terDimension)
            {
                heightParallelToCorner = hieght + (-1 * (borderHieghtModUpperRight) * (x - (terX + terDimension)));
                borderHieghtMod = (heightParallelToCorner - heightParallelToBorder) / terMargin;
            }

            int step = 0;

            for(float y = terZ + terDimension + terMargin; y > terZ + terDimension; y--)
            {
                newHeight = heightParallelToBorder + borderHieghtMod * (step);
                locs.add(new Vector2f(x, y));
                if((x >= terX) && (x <= terX + terDimension))
                {
                    newHeight = (((newHeight >= heightParallelToBorder) && (newHeight < hieght)) || ((newHeight <= heightParallelToBorder) && (newHeight > hieght))) ? newHeight
                            : heightParallelToBorder;
                }
                heights.add(newHeight);
                step += 1;
            }
        }

        terrain.setHeight(locs, heights);
        terrain.updateModelBound();
        locs.clear();
        heights.clear();
    }

    private static void createTerrain()
    {
        Application app = Visualizer3DApplication.getInstance().getApp();
        Random generator = new Random();
        int hillCount = generator.nextInt(3500) + 1500; // The range of the
                                                        // hills generated
                                                        // randomly
        int hillMinRadius = generator.nextInt(40) + 30; // The range for the
                                                        // minimum hill radius
        int hillMaxRadius = generator.nextInt(40) + 80; // The range for the
                                                        // maximum hill radius
        terMargin = generator.nextInt(150) + 150;
        float grassScale = 64;
        float dirtScale = 16;
        float rockScale = 128;
        // boolean wardiso = false;
        // Vector3f envFloorPos;
        // Vector3f envFloorScale;
        xt = -420;
        zt = -420;
        dim = 900f;
        alt = 200f;
        terMargin = 300;
        AssetManager assetManager = (app.getAssetManager());
        // First, we load up our textures and the heightmap texture for the
        // terrain

        // TERRAIN TEXTURE material
        matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matTerrain.setBoolean("useTriPlanarMapping", false);
        matTerrain.setBoolean("WardIso", true);
        matTerrain.setFloat("Shininess", 0);

        // ALPHA map (for splat textures)
        matTerrain.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap", grass);
        matTerrain.setFloat("DiffuseMap_0_scale", grassScale);

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_1", dirt);
        matTerrain.setFloat("DiffuseMap_1_scale", dirtScale);

        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        matTerrain.setTexture("DiffuseMap_2", rock);
        matTerrain.setFloat("DiffuseMap_2_scale", rockScale);

        AbstractHeightMap heightmap = null;
        try
        {

            heightmap = new HillHeightMap(1025, hillCount, hillMinRadius, hillMaxRadius, (byte) 1);
            heightmap.load();
            heightmap.smooth(0.9f, 1);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // CREATE THE TERRAIN
        terrain = new TerrainQuad("terrain", 65, 1025, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, app.getCamera());
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch
                                                                       // size,
                                                                       // and a
                                                                       // multiplier
        terrain.addControl(control);
        terrain.setMaterial(matTerrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2.5f, 0.5f, 2.5f);
        Node node = ((BaseApplication<?, ?>) app).getRootNode();
        node.attachChild(terrain);

        // Create the flat area in the terrain and make the margins look smooth
        adjustSimulationArea(xt, zt, dim, alt);
    }

    /**
     * Loads the terrain in the 3D visualization
     */
    public static void loadTerrain()
    {

        try
        {
            Visualizer3DApplication.getInstance().getApp().enqueue(new Callable<Object>()
            {

                @Override
                public Object call() throws Exception
                {
                    createTerrain();
                    return null;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Unloads the terrain from the 3D visualization
     */
    public static void unloadTerrain()
    {

        try
        {
            Visualizer3DApplication.getInstance().getApp().enqueue(new Callable<Object>()
            {

                @Override
                public Object call() throws Exception
                {
                    detachTerrain();
                    return null;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void detachTerrain()
    {
        Visualizer3DApplication.getInstance().getApp().getRootNode().detachChild(terrain);
    }

    /**
     * Gets the application's terrain
     * 
     * @return the terrain of the applicaiton
     */
    public static TerrainQuad getTerrain()
    {
        return terrain;
    }

}
