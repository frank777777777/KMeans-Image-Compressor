
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;


public class KMeans {
    public static void main(String [] args){
        if (args.length < 3){
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try{
            File file = new File(args[0]);
            System.out.println(file.getPath());
            BufferedImage originalImage = ImageIO.read(file);
            int k=Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[2]));

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
        int w=originalImage.getWidth();
        int h=originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w,h , null);
        // Read rgb values from the image
        int[] rgb=new int[w*h];
        int count=0;
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                rgb[count++]=kmeansImage.getRGB(i,j);
            }
        }

//        IndexColorModel new_palette = new IndexColorModel(3,8,)

        // Call kmeans algorithm: update the rgb values
        kmeans(rgb,k);

        // Write the new rgb values to the image
        count=0;
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                kmeansImage.setRGB(i,j,rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k)
    {

        //initiate the color array
        Color[] colors = new Color[rgb.length];
        for (int i=0; i<rgb.length; i++)
        {
            colors[i] = new Color(rgb[i]);
        }

        //the new colors that will be assigned to the pixels
//        Color[] new_color = new Color[k];
        // initialize an array of indexes of centroid
        int[] c = new int[rgb.length];
        //the total_cost of all pixels, which we try to minimize
//        long total_cost= Long.MAX_VALUE;
        // get colors of the centroids
        Color[] centroids = new Color[k];

        Random rand = new Random();
        // generate k random numbers as centroids
        int[] random_centroids = rand.ints(0,rgb.length).distinct().limit(k).toArray();

        // assign random colors to centroids
        for (int i=0; i<k; i++)
        {
            centroids[i] = colors[random_centroids[i]];
        }

        //min cost of the last iteration
        long last_min_cost=Long.MAX_VALUE;
        boolean done_flag=false;
        while(done_flag==false)
        {
            long min_cost=0;
            for(int i=0; i<rgb.length; i++)
            {
                // tem value to find the minimum cost of the centroid
                long min = Long.MAX_VALUE;
                int cost;
                for(int j=0; j<k; j++)
                {
                    //calculate cost function
                    int diff_red = colors[i].getRed()-centroids[j].getRed();
                    int diff_green = colors[i].getGreen()-centroids[j].getGreen();
                    int diff_blue = colors[i].getBlue()-centroids[j].getBlue();
                    cost = diff_red*diff_red + diff_green*diff_green + diff_blue*diff_blue;
                    //find the minimum cost centroid, and assign the centroid index to its array
                    if(cost<min)
                    {
                        min = cost;
                        c[i] = j;
                    }
                }
                min_cost +=min;
            }
            //stop iteration if there is no improvement in min_cost, else keep looping
            if(last_min_cost == min_cost)
                done_flag = true;
            else
                last_min_cost = min_cost;

            //get the average rgb for each centroid
            //initialize arrays for r,g,b, and count of the pixels associated with it
            int[] r = new int[k];
            int[] g = new int[k];
            int[] b = new int[k];
            int[] count = new int[k];
            for(int i=0; i<rgb.length; i++)
            {
                int index = c[i];
                r[index] += colors[i].getRed();
                g[index] += colors[i].getGreen();
                b[index] += colors[i].getBlue();

                count[index] +=1;
            }
            // calculate the mean of rgb values
            for (int i=0;i<k;i++)
            {
                if(count[i]!=0)
                {
                    r[i] = r[i]/count[i];
                    g[i] = g[i]/count[i];
                    b[i] = b[i]/count[i];
                }
                centroids[i] = new Color(r[i],g[i],b[i]);
            }
        }

        //replace all pixels to its closest centroids
        for(int i=0; i<rgb.length; i++)
        {
            rgb[i]=centroids[c[i]].getRGB();
        }




    }

}