package de.maxgb.minecraft.second_screen.world;

import java.util.ArrayList;
import java.util.HashMap;

public class ObservingRegistry {
	private static HashMap<String,ObservedBlock> map;
	
	/**
	 * Adds a block to the observing list, overrides blocks with the same label
	 * @param label Label 
	 * @param x
	 * @param y
	 * @param z
	 * @return false if there already was a observed block with that label
	 */
	public static boolean observeBlock(String label,int x,int y,int z){
		if(map==null){
			map=new HashMap<String,ObservedBlock>();
		}
		return (map.put(label, new ObservedBlock(label,x,y,z))==null);
	}
	
	/**
	 * Removes the block with the given label from the observing list
	 * @param label
	 * @return if block was removed
	 */
	public static boolean removeObservedBlock(String label){
		return (map.remove(label)!=null);
	}
	
	
	public static ArrayList<ObservedBlock> getObservedBlocks(){
		if(map==null){
			map=new HashMap<String,ObservedBlock>();
		}
		
		ArrayList<ObservedBlock> blocks= new ArrayList<ObservedBlock>();
		blocks.addAll(map.values());
		return blocks;
	}
	
	public static class ObservedBlock{
		public String label;
		public int x,y,z;
		
		public ObservedBlock(String label,int x,int y,int z){
			this.x=x;
			this.y=y;
			this.z=z;
			this.label=label;
		}
		
	}
	
	
}
