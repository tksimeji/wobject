package com.tksimeji.wobject.test;

import com.tksimeji.wobject.api.Component;
import com.tksimeji.wobject.api.Wobject;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Wobject(name = "sample")
public final class SampleWobject {
    @Component(type = Material.STONE)
    public Block Stone;

    @Component(type = {Material.END_STONE, Material.END_STONE_BRICKS})
    public Block EndStone;
}
