package me.titan.core.utils.cuboid;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Iterator;

@SuppressWarnings("ConstantConditions")
public class CuboidLocationIterator implements Iterator<Location> {
    private final int baseZ;
    private final World world;
    private int z;
    private final int sizeZ;
    private final int sizeY;
    private final int baseY;
    private final int baseX;
    private int x;
    private int y;
    private final int sizeX;
    
    @Override
    public boolean hasNext() {
        return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
    }
    
    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public CuboidLocationIterator(World world, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
        this.world = world;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseZ = baseZ;
        this.sizeX = Math.abs(sizeX - baseX) + 1;
        this.sizeY = Math.abs(sizeY - baseY) + 1;
        this.sizeZ = Math.abs(sizeZ - baseZ) + 1;
        boolean b = false;
        this.z = (b ? 1 : 0);
        this.y = (b ? 1 : 0);
        this.x = (b ? 1 : 0);
    }
    
    @Override
    public Location next() {
        Location location = new Location(this.world, this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
        if (++this.x >= this.sizeX) {
            this.x = 0;
            if (++this.y >= this.sizeY) {
                this.y = 0;
                ++this.z;
            }
        }
        return location;
    }
}
