package de.maxgb.minecraft.second_screen.info_listener;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.User;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Listener which listens to player information like health or position
 * @author Max
 *
 */
public class PlayerInfoListener extends StandardListener {

	@SuppressWarnings("unused")
	private final String TAG = "PlayerInfoListener";

	public PlayerInfoListener(User user) {
		super(user);
		everyTick = Configs.player_info_update_time;

	}

	@Override
	public String update() {
		JSONObject response = new JSONObject();

		EntityPlayerMP player = user.getPlayer(server);
		

		if (player == null) {
			response.put("success", 0).put("error", "User " + user.username + " not online");
		} else {
            int x = MathHelper.floor(player.posX);
            int y = MathHelper.floor(player.posY);
            int z = MathHelper.floor(player.posZ);
            
			response.put("health", player.getHealth());
			response.put("foodlevel", player.getFoodStats().getFoodLevel());
			response.put("eplevel", player.experienceLevel);
			response.put("posx", Integer.valueOf(x));
			response.put("posy", Integer.valueOf(y));
			response.put("posz", Integer.valueOf(z));
			response.put("posxc", Integer.valueOf(x & 15));
			response.put("poszc", Integer.valueOf(z & 15));
			response.put("ping", player.ping);
			
			//Get direction, @see GUIIngame
			
            EnumFacing enumfacing = player.getHorizontalFacing();
			response.put("direction", enumfacing.getName());

            if (player.getEntityWorld() != null && player.getEntityWorld().isBlockLoaded(player.getPosition()))
            {
                Chunk chunk = player.getEntityWorld().getChunkFromBlockCoords(player.getPosition());
                response.put("lc", (chunk.getTopFilledSegment()+15));
                response.put("biome", chunk.getBiome(player.getPosition(), player.getEntityWorld().getBiomeProvider()).getBiomeName());
                response.put("light", chunk.getLightSubtracted(player.getPosition(), 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, player.getPosition()) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, player.getPosition()) + " block)");


			}

			JSONArray potions = new JSONArray();
			Collection<PotionEffect> pot = player.getActivePotionEffects();
			for (PotionEffect i : pot) {
				JSONArray p = new JSONArray();
                p.put(I18n.translateToLocal(i.getEffectName()));
                p.put(i.getDuration() / 20);
				potions.put(p);
			}
			response.put("potions", potions);

			response.put("success", 1);
		}

		return PROTOKOLL.S_PLAYERINFO_LISTENER + "-" + response.toString();

	}

}
