package me.alphamode.mcbig.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.phys.Vec3;

public class Vec3Argument implements ArgumentType<BigCoordinate> {
    @Override
    public BigCoordinate parse(StringReader reader) throws CommandSyntaxException {
        double x = reader.readDouble();
        reader.skip();
        double y = reader.readDouble();
        reader.skip();
        double z = reader.readDouble();
        return null;//Vec3.newTemp(x, y, z);
    }
}
