package com.woolplacer.client;

import com.woolplacer.WoolPlacerMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class WoolPlacerClient implements ClientModInitializer {

    private static KeyBinding toggleKey;
    private static boolean isEnabled = false;

    // Маппинг предметов шерсти → блоки шерсти
    private static final Map<Item, Block> WOOL_MAP = new HashMap<>();

    static {
        WOOL_MAP.put(net.minecraft.item.Items.WHITE_WOOL,      Blocks.WHITE_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.ORANGE_WOOL,     Blocks.ORANGE_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.MAGENTA_WOOL,    Blocks.MAGENTA_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.YELLOW_WOOL,     Blocks.YELLOW_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.LIME_WOOL,       Blocks.LIME_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.PINK_WOOL,       Blocks.PINK_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.GRAY_WOOL,       Blocks.GRAY_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.CYAN_WOOL,       Blocks.CYAN_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.PURPLE_WOOL,     Blocks.PURPLE_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.BLUE_WOOL,       Blocks.BLUE_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.BROWN_WOOL,      Blocks.BROWN_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.GREEN_WOOL,      Blocks.GREEN_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.RED_WOOL,        Blocks.RED_WOOL);
        WOOL_MAP.put(net.minecraft.item.Items.BLACK_WOOL,      Blocks.BLACK_WOOL);
    }

    @Override
    public void onInitializeClient() {
        // Регистрируем клавишу G для включения/выключения
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.woolplacer.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.woolplacer"
        ));

        // Каждый тик клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Обработка нажатия клавиши
            while (toggleKey.wasPressed()) {
                isEnabled = !isEnabled;
                if (client.player != null) {
                    String status = isEnabled ? "§aВКЛЮЧЕН" : "§cВЫКЛЮЧЕН";
                    client.player.sendMessage(new LiteralText("§6[WoolPlacer] §fРежим укладки: " + status), true);
                }
            }

            // Логика укладки блоков
            if (!isEnabled) return;
            if (client.player == null || client.world == null) return;

            // Только если игрок на земле
            if (!client.player.isOnGround()) return;

            // Получаем предмет в руке
            ItemStack heldItem = client.player.getMainHandStack();
            Item heldItemType = heldItem.getItem();

            // Проверяем, является ли предмет шерстью
            Block woolBlock = WOOL_MAP.get(heldItemType);
            if (woolBlock == null) return;

            // Позиция под ногами игрока
            BlockPos playerPos = client.player.getBlockPos();
            BlockPos belowPos = playerPos.down();

            World world = client.world;

            // Ставим блок только если там воздух
            if (world.getBlockState(belowPos).isAir()) {
                world.setBlockState(belowPos, woolBlock.getDefaultState());
            }
        });

        WoolPlacerMod.LOGGER.info("WoolPlacer client initialized!");
    }
}
