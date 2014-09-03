package de.maxgb.minecraft.second_screen.info_listener;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.User;

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
			int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.posY);
            int z = MathHelper.floor_double(player.posZ);
            
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
			int i4 = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			response.put("direction", i4 + " (" + Direction.directions[i4] + ")");
			
			if (player.worldObj != null && player.worldObj.blockExists(x,y,z))
            {
                Chunk chunk = player.worldObj.getChunkFromBlockCoords(x, z);
                response.put("lc", (chunk.getTopFilledSegment()+15));
                response.put("biome", chunk.getBiomeGenForWorldCoords(x & 15, z & 15, player.worldObj.getWorldChunkManager()).biomeName);
                response.put("blight", chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15));
                response.put("slight", chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15));
                response.put("rlight", chunk.getBlockLightValue(x & 15, y, z & 15, 0));
                
                
            }

			JSONArray potions = new JSONArray();
			Collection<PotionEffect> pot = player.getActivePotionEffects();
			for (PotionEffect i : pot) {
				JSONArray p = new JSONArray();
				p.put(StatCollector.translateToLocal(i.getEffectName()));
				p.put(i.getDuration() / 20);
				potions.put(p);
			}
			response.put("potions", potions);

			response.put("success", 1);
		}

		return PROTOKOLL.S_PLAYERINFO_LISTENER + "-" + response.toString();

	}

}
