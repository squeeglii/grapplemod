package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.client.ClientSetup;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.item.KeypressItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientHookMixin {

    public boolean[] keyPressHistory = { false, false, false, false, false };


    @Inject(method = "tick()V", at = @At("TAIL"))
    public void clientTickHook(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (!Minecraft.getInstance().isPaused()) {
                ClientControllerManager.instance.onClientTick(player);

                if (Minecraft.getInstance().screen == null) {
                    // keep in same order as enum from KeypressItem
                    boolean[] keys = {
                            ClientSetup.key_enderlaunch.isDown(), ClientSetup.key_leftthrow.isDown(),
                            ClientSetup.key_rightthrow.isDown(), ClientSetup.key_boththrow.isDown(),
                            ClientSetup.key_rocket.isDown()
                    };

                    for (int i = 0; i < keys.length; i++) {
                        boolean isKeyDown = keys[i];
                        boolean prevKey = this.keyPressHistory[i];

                        if (isKeyDown != prevKey) {
                            KeypressItem.Keys key = KeypressItem.Keys.values()[i];

                            ItemStack stack = this.getKeypressStack(player);
                            if (stack != null) {
                                if (!this.isLookingAtModifierBlock(player)) {
                                    if (isKeyDown) {
                                        ((KeypressItem) stack.getItem()).onCustomKeyDown(stack, player, key, true);
                                    } else {
                                        ((KeypressItem) stack.getItem()).onCustomKeyUp(stack, player, key, true);
                                    }
                                }
                            }
                        }

                        this.keyPressHistory[i] = isKeyDown;
                    }
                }
            }
        }
    }



    public ItemStack getKeypressStack(Player player) {
        if (player == null) return null;

        ItemStack stack;

        stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (stack.getItem() instanceof KeypressItem) return stack;

        stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof KeypressItem) return stack;

        return null;
    }

    public boolean isLookingAtModifierBlock(Player player) {
        HitResult result = Minecraft.getInstance().hitResult;
        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bray = (BlockHitResult) result;
            BlockPos pos = bray.getBlockPos();
            BlockState state = player.level.getBlockState(pos);

            return (state.getBlock() == CommonSetup.grappleModifierBlock.get());
        }
        return false;
    }
}
