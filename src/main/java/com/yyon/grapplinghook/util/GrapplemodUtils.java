package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.common.CommonSetup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;

public class GrapplemodUtils {

	private static int controllerid = 0;
	public static final int GRAPPLE_ID = controllerid++;
	public static final int REPEL_ID = controllerid++;
	public static final int AIR_FRICTION_ID = controllerid++;

	public static void sendToCorrectClient(Object message, int playerid, Level w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayer) {
			CommonSetup.network.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity), message);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}

	public static BlockHitResult rayTraceBlocks(Level world, Vec from, Vec to) {
		HitResult result = world.clip(new ClipContext(from.toVec3d(), to.toVec3d(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
		if (result != null && result instanceof BlockHitResult) {
			BlockHitResult blockhit = (BlockHitResult) result;
			if (blockhit.getType() != HitResult.Type.BLOCK) {
				return null;
			}
			return blockhit;
		}
		return null;
	}

	public static long getTime(Level w) {
		return w.getGameTime();
	}

	public static boolean and(Boolean... conditions) {
		boolean failed = Arrays.stream(conditions).anyMatch(bool -> !bool);
		return !failed;
	}

}
