/**
 * 
 */
package net.ambulando.image.search.surf;

/**
 * @author massi
 *
 */
public interface IntegralImage {

    float get(int x, int y);
    
    int getHeight();
    
    int getWidth();
    
    float area(int x1, int y1, int rectWidth, int rectHeight);
}
