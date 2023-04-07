package xyz.raitaki.rdungeons.utils;

import com.fastasyncworldedit.core.function.mask.BlockMaskBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.Location;
import xyz.raitaki.rdungeons.RDungeons;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Schematicutils {

    public static void setBlock(Location location, BlockType blockType) {
        World world = BukkitAdapter.adapt(location.getWorld());

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .build();

        editSession.smartSetBlock(BukkitAdapter.asVector(location).toBlockPoint(), new BaseBlock(blockType.getDefaultState()));
        editSession.flushQueue();
        editSession.close();
    }

    public static void pasteSchem(Location location, String name, int x, int y, int z) {
        World world = BukkitAdapter.adapt(location.getWorld());
        File schemFile;
        File schemFolder = RDungeons.getSchemFolder();
        if (!new File(schemFolder + "/" + name + ".schematic").exists()) {
            if (!new File(schemFolder + "/" + name + ".schem").exists()) {
                return;
            } else {
                schemFile = new File(schemFolder + "/" + name + ".schem");
            }
        } else {
            schemFile = new File(schemFolder + "/" + name + ".schematic");
        }

        ClipboardFormat schematic = ClipboardFormats.findByFile(schemFile);
        Clipboard clipboard;
        try {
            ClipboardReader reader = schematic.getReader(new FileInputStream(schemFile));
            clipboard = reader.read();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        AffineTransform transform = new AffineTransform().rotateX(x).rotateY(y).rotateZ(z);

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .build();

        ClipboardHolder holder = new ClipboardHolder(clipboard);
        holder.setTransform(holder.getTransform().combine(transform));

        Operation operation = holder
                .createPaste(editSession)
                .to(BukkitAdapter.asVector(location).toBlockPoint())
                .build();

        Operations.complete(operation);
        editSession.flushQueue();
        editSession.close();
    }

    public static void replaceBlocks(Location minPoint, Location maxPoint, BlockType blockMask, Pattern pattern) {
        World world = BukkitAdapter.adapt(minPoint.getWorld());

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .build();

        CuboidRegion region = new CuboidRegion(world, BukkitAdapter.asBlockVector(new Location(minPoint.getWorld(), minPoint.getBlockX()-17, -64, minPoint.getBlockZ()-17)), BukkitAdapter.asBlockVector(new Location(maxPoint.getWorld(), maxPoint.getBlockX()+17, 319, maxPoint.getBlockZ()+17)));
        Mask mask = new BlockMaskBuilder().addTypes(blockMask).build(editSession.getExtent());

        editSession.replaceBlocks(region, mask, pattern);
        editSession.flushQueue();
        editSession.close();
    }

    public static List<Location> scanBlocks(Location minPoint, Location maxPoint, BlockType blockMask) {
        World world = BukkitAdapter.adapt(minPoint.getWorld());

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(world)
                .fastMode(true)
                .build();

        List<Location> blocksLocations = new ArrayList<>();
        new CuboidRegion(world, BukkitAdapter.asBlockVector(minPoint), BukkitAdapter.asBlockVector(maxPoint)).forEach(vec -> {
            if (vec.getFullBlock(editSession.getExtent()).getBlockType().equals(blockMask)) {
                blocksLocations.add(new Location(minPoint.getWorld(), vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()));
            }
        });

        return blocksLocations;
    }

}
