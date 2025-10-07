package com.baizeli.eternisstarrysky.Items;

import com.baizeli.Sounds;
import com.baizeli.eternisstarrysky.EntityMarker;
import com.baizeli.eternisstarrysky.RainbowEffectHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.baizeli.eternisstarrysky.RainbowEffectHelper.BLUE;

public final class InfinitySwordTrue extends SwordItem
{
	private static final double KILL_RADIUS = 5;

    //归零的数据
	// public static List datas = new ArrayList();

	public InfinitySwordTrue(Tier tier, int attackDamage, float attackSpeed, Properties properties)
	{
		super(tier, attackDamage, attackSpeed, properties.rarity(Rarity.EPIC).fireResistant());
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack)
	{
		return UseAnim.BLOCK;
	}

	@Override
	public int getUseDuration(ItemStack stack)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isFoil(ItemStack stack)
	{
		return true;
	}

	@Override
	public boolean isDamageable(ItemStack stack)
	{
		return false;
	}

	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return 0;
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level level, Player player)
	{
		super.onCraftedBy(stack, level, player);
		stack.getOrCreateTag().putInt("HideFlags", 2);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
	{
		super.inventoryTick(stack, level, entity, slotId, isSelected);
		if (!stack.getOrCreateTag().contains("HideFlags"))
		{
			stack.getOrCreateTag().putInt("HideFlags", 2);
		}
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);

		player.startUsingItem(hand);
		return InteractionResultHolder.consume(stack);
	}

	// 格挡松手 - 结束格挡
	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity2, int timeLeft)
	{
		if (!(entity2 instanceof Player player))
			return;

		if (!(entity2 instanceof ServerPlayer))
			Sounds.play(SoundEvents.GLASS_BREAK, player, 1.0F, 0.8F);

		if (entity2 instanceof Player)
		{
			if (!level.isClientSide)
			{
				Vec3 playerPos = player.position();
				double radius = 17.0;

				AABB searchArea = new AABB(
					playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
					playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
				);

				List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchArea,
					entity -> entity != null
						&& entity != player
						&& entity.distanceTo(player) <= radius
						&& !(entity instanceof Player)); // 排除所有玩家

				nearbyEntities.sort((e1, e2) -> Float.compare(e1.distanceTo(player), e2.distanceTo(player)));

				List<LivingEntity> targetsToKill = nearbyEntities.subList(0, Math.min(9, nearbyEntities.size()));

				for (int i = 0; i < 50; i++)
				{
					double offsetX = (level.random.nextDouble() - 0.5) * radius * 2;
					double offsetY = (level.random.nextDouble() - 0.5) * radius * 2;
					double offsetZ = (level.random.nextDouble() - 0.5) * radius * 2;

					level.addParticle(ParticleTypes.EXPLOSION_EMITTER,
						playerPos.x + offsetX, playerPos.y + offsetY, playerPos.z + offsetZ,
						0, 0, 0);
					level.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
						playerPos.x + offsetX, playerPos.y + offsetY, playerPos.z + offsetZ,
						0, 0.1, 0);
				}

				if (targetsToKill instanceof ArmorStand)
				{
					return;
				}

				// 排除展示框
				if (targetsToKill instanceof ItemFrame)
				{
					return;
				}

				// 排除画
				if (targetsToKill instanceof Painting)
				{
					return;
				}

				for (LivingEntity entity : targetsToKill)
				{

					if (!(entity instanceof Monster) && !(entity instanceof Player))
					{
						return;
					}

					if (entity.isAlive() && entity != player)
					{
						entity.removeAllEffects();
						entity.invulnerableTime = 0;
						entity.hurtTime = 0;

						//虚空伤害
						DamageSource void_damage = new DamageSource(entity.damageSources().fellOutOfWorld().typeHolder(),player);

						entity.hurt(void_damage, Float.MAX_VALUE);
						entity.setHealth(0);
						entity.kill();
						entity.remove(Entity.RemovalReason.KILLED);

						ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), entity.getExperienceReward());
						entity.dropCustomDeathLoot(entity.damageSources().playerAttack(player), 10, true);
						entity.dropFromLootTable(entity.damageSources().playerAttack(player), true);

						for (int i = 0; i < 10; i++)
						{
							level.addParticle(ParticleTypes.LARGE_SMOKE,
								entity.getX() + (level.random.nextDouble() - 0.5) * 2,
								entity.getY() + level.random.nextDouble() * 2,
								entity.getZ() + (level.random.nextDouble() - 0.5) * 2,
								0, 0.1, 0);
						}
					}
				}

				if (!targetsToKill.isEmpty())
				{
					level.playSound(null, player.getX(), player.getY(), player.getZ(),
						SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.0F, 0.1F);
				}
			}

			Vec3 playerPos = player.position();
			double radius = 20.0;
			AABB searchArea = new AABB(
				playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
				playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
			);

			List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, searchArea);
			int itemCount = 0;

			for (ItemEntity itemEntity : itemEntities)
			{
				double distance = itemEntity.position().distanceTo(playerPos);
				if (distance <= radius)
				{
					//itemEntity.setPos(playerPos.x, playerPos.y, playerPos.z);
					if (player.getInventory().getFreeSlot()>0) {
						player.addItem(itemEntity.getItem());
						itemEntity.remove(Entity.RemovalReason.DISCARDED);
					}
					itemCount++;
				}
			}

			if (!level.isClientSide && itemCount > 0)
			{
				player.displayClientMessage(
					Component.literal("§6吸取了 §e" + itemCount + " §6个掉落物！"),
					true
				);
			}
		}
	}

	@Override
	public Component getName(ItemStack stack)
	{
		Component originalName = super.getName(stack);
		if (stack.hasCustomHoverName())
		{
			return originalName;
		}
		stack.getOrCreateTag().putInt("HideFlags", 2);
		return originalName;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
	{
		if (!(player instanceof ServerPlayer))
		{
			Sounds.play(SoundEvents.AMETHYST_BLOCK_STEP, player, 10.0F, 1.0F);
			if (entity instanceof LivingEntity)
				EntityMarker.mark(entity, EntityMarker.ENTITY_DATA_HEALTH);
			return false;
		}

		if (!(entity instanceof LivingEntity))
			return false;

		DamageSource voidDamage = new DamageSource(entity.damageSources().fellOutOfWorld().typeHolder(), player);
		Predicate<LivingEntity> predicate = (e) -> e.getId() != player.getId();
		List<LivingEntity> nearbyEntities = InfinitySword.getNearbyLivingEntities(entity, KILL_RADIUS, predicate);
		nearbyEntities.forEach(e -> {
			player.crit(e);
			e.hurt(voidDamage, Float.MAX_VALUE);
		});

		InfinitySword.sweep(player, entity, stack, Float.MAX_VALUE);

		player.resetAttackStrengthTicker();

		EntityMarker.mark(entity, EntityMarker.ENTITY_DATA_HEALTH);
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
	{

		tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.infinity_sword2").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2F));

		tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("AIR").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2f));
		tooltip.add(Component.translatable("item.isMain"));
		tooltip.add(RainbowEffectHelper.createCustomGradientText(Component.translatable("item.eternisstarrysky.infinity_sword_damage_true").getString(), RainbowEffectHelper.DEFAULT_RAINBOW, 2, 1, 0.05F, 2F).append(Component.translatable("item.attDamage").getString()));
		tooltip.add(Component.literal(" §22.0").append(Component.translatable("item.attSpeed").getString()));

		/*if (Screen.hasShiftDown())
		{
			tooltip.add(Component.empty());
			//tooltip.add(Component.literal("详细:").withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.literal("右键点击则击杀以你为中心").withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.literal("半径17格范围内的所有生物").withStyle(ChatFormatting.GRAY));
		}
		else
		{
			tooltip.add(Component.empty());
			tooltip.add(Component.literal("按住 Shift 查看更多信息").withStyle(ChatFormatting.GRAY));
		}*/
	}
	//上下移动的字体
	Font font = null;
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {

		consumer.accept(new IClientItemExtensions() {
			@Override
			public @org.jetbrains.annotations.Nullable Font getFont(ItemStack stack, FontContext context) {
				if (font == null)font=new SinFont(Minecraft.getInstance().font.fonts,true);
				return font;
			}
		});
		super.initializeClient(consumer);
	}
	//彩色字体
	public class SinFont extends Font {
		private static long getTimeBase() {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.level != null) return minecraft.level.getGameTime();
			return System.currentTimeMillis() / 50;
		}
		public SinFont(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
			super(p_243253_, p_243245_);
		}

		/*@Override
        public int drawInBatch(String p_272751_, float p_272661_, float p_273129_, int p_273272_, boolean p_273209_, Matrix4f p_272940_, MultiBufferSource p_273017_, DisplayMode p_272608_, int p_273365_, int p_272755_) {
            return super.drawInBatch(p_272751_, p_272661_, p_273129_+ Mth.sin(Minecraft.getInstance().getFrameTime()), p_273272_, p_273209_, p_272940_, p_273017_, p_272608_, p_273365_, p_272755_);
        }*/
		private static int getGradientColor(List<Integer> colors, float position) {
			int colorCount = colors.size();
			float segment = 1f / (colorCount - 1);

			int segmentIndex = (int) (position / segment);
			if (segmentIndex >= colorCount - 1) return colors.get(colorCount - 1);
			float segmentPos = (position % segment) / segment;
			int startColor = colors.get(segmentIndex);
			int endColor = colors.get(segmentIndex + 1);

			return interpolateColor(startColor, endColor, segmentPos);
		}

		private static int interpolateColor(int start, int end, float progress) {
			int startR = (start >> 16) & 0xFF;
			int startG = (start >> 8) & 0xFF;
			int startB = start & 0xFF;

			int endR = (end >> 16) & 0xFF;
			int endG = (end >> 8) & 0xFF;
			int endB = end & 0xFF;

			int r = (int) (startR + (endR - startR) * progress);
			int g = (int) (startG + (endG - startG) * progress);
			int b = (int) (startB + (endB - startB) * progress);

			return (r << 16) | (g << 8) | b;
		}

		@Override
		public int drawInBatch(@NotNull FormattedCharSequence formattedCharSequence, float x, float y, int rgb, boolean b1, @NotNull Matrix4f matrix4f, @NotNull MultiBufferSource multiBufferSource, @NotNull Font.DisplayMode mode, int i, int i1) {
			StringBuilder stringBuilder = new StringBuilder();
			formattedCharSequence.accept((index, style, codePoint) -> {
				stringBuilder.appendCodePoint(codePoint);
				return true;
			});
			String text = ChatFormatting.stripFormatting(stringBuilder.toString());
			float hue = (float) Util.getMillis() / 52000.0F % 1.0F;
			float hueStep = (float)(0.025D + (Math.sin(((float)Util.getMillis() / 1200.0F)) % 6.28318D + 0.9D) * 0.1475D / 3.6D /1.5);
			if (text != null) {
				for (int index = 0; index < text.length(); index++) {
					String s = String.valueOf(text.charAt(index));
					//y变化量
					float yOffset = (float) (Math.sin(index + (Util.getMillis() / 520f)) * 1.5);
					//x变化量
					float xOffset = (float) (Math.cos(i + (Util.getMillis() / 600f)) * 2);
					//int color = rgb & 0xFF002222 | Mth.hsvToRgb(hue, 0.5F, 0.9f);
					int direction = 1;
					int speed = 3;
					float gradientSpan = 1F;
					float charSpacing = 0.03F;
					long time = getTimeBase();
					int index1 = (direction > 0) ? i : text.length() - 1 - i;
					float position = ((time * speed+index) / 100f + i * charSpacing) % 1f;
					position = position * gradientSpan % 1f;
					int color = getGradientColor(BLUE, position);
					//color ^= 0x00ff0000;
					//彩色渲染
					if((text.contains(I18n.get("item.eternisstarrysky.infinity_sword_damage_true")) && I18n.get("item.eternisstarrysky.infinity_sword_damage_true").contains(s)) | (text.contains(I18n.get("item.eternisstarrysky.infinity_sword_true")) && I18n.get("item.eternisstarrysky.infinity_sword_true").contains(s)))
						super.drawInBatch(s,  x ,y+  yOffset, color, b1, matrix4f, multiBufferSource, mode, i, i1);
					 //灰色字渲染
					else super.drawInBatch(s,  x , y, ChatFormatting.GRAY.getColor(), b1, matrix4f, multiBufferSource, mode, i, i1);
					//绿色字体渲染
					if ((text.contains(I18n.get("attribute.name.generic.attack_speed")) && I18n.get("attribute.name.generic.attack_speed").contains(s)) | (text.contains(I18n.get("attribute.name.generic.attack_damage")) && I18n.get("attribute.name.generic.attack_damage").contains(s)))super.drawInBatch(s,  x ,y, ChatFormatting.DARK_GREEN.getColor(), b1, matrix4f, multiBufferSource, mode, i, i1);
					if (text.contains("2.0"))super.drawInBatch(s,  x ,y, ChatFormatting.DARK_GREEN.getColor(), b1, matrix4f, multiBufferSource, mode, i, i1);

					if (text.contains(I18n.get("item.eternisstarrysky.infinity_sword2")))super.drawInBatch(s,  x , y, ChatFormatting.DARK_RED.getColor(), b1, matrix4f, multiBufferSource, mode, i, i1);

					hue += hueStep;
					hue %= 1.0F;
					x += width(s);
				}
			}
			return (int)x;
		}
	}
}