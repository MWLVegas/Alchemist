package com.ivalicemud.alchemist;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Alchemist extends JavaPlugin implements Listener
{
	boolean flatfile = false;
	boolean setup = false;
    public static Alchemist plugin;
	

	
    public Alchemist()
    {
    }
    
    
    


    public void onEnable()
    {
    	 plugin = this;
    	 loadConfig();
    
    	 getServer().getPluginManager().registerEvents(plugin, this);
    	 
    }
    
    public void msg(CommandSender s, Object msg )
    {
    	s.sendMessage( msg.toString().replaceAll("&([0-9A-Fa-f])", "\u00A7$1") );
    }
    public void error(Object msg) {
		String txt = "[Alchemist Error] " + msg.toString();
		Bukkit.getServer().getLogger().info( txt.replaceAll("&([0-9A-Fa-f])", "\u00A7$1") );

	}

	public void debug(Object msg) {
		if (plugin.getConfig().getBoolean("main.debug") == true) {
			String txt = "[Alchemist Debug] " + msg.toString();
			Bukkit.getServer().getLogger().info( txt.replaceAll("&([0-9A-Fa-f])", "\u00A7$1") );
		}
	}
	
	public void log(Object msg) {
		if (plugin.getConfig().getBoolean("main.debug") == true) {
			String txt = "[Alchemist] " + msg.toString();
			Bukkit.getServer().getLogger().info( txt.replaceAll("&([0-9A-Fa-f])", "\u00A7$1") );
		}
	}
	
    public void loadConfig()
    {
    	
    	setConfig("main.debug",false);
    	setConfig("main.maxEffects",3);
    	setConfig("main.maxStackedEffects",3);
    	
    	setConfig("potion.allowWater",false);
    	setConfig("potion.allowSplash", false);
    	
    	setConfig("potion.388.effect","FAST_DIGGING");
    	setConfig("potion.388.duration",60);
    	setConfig("potion.388.power",1);
    	setConfig("potion.388.requires",0);
    	setConfig("potion.388.overwrite",true);
    	setConfig("potion.388.name","Giga Drill");
    	
   /*
    	setConfig("wand.388.effect","FAST_DIGGING");
    	setConfig("wand.388.duration",60);
    	setConfig("wand.388.uses",3);
    	setConfig("wand.388.name","Giga Drill");
    	setConfig("wand.388.power",1);
    	
   
    
    	setConfig("grenade.388.duration",60);
    	setConfig("grenade.388.name","Giga Drill");
    	setConfig("grenade.388.power",1);
    	setConfig("grenade.388.range",5);
    */	
    	
    	saveConfig();
    	log("Loading Alchemist Recipes ...");
     	loadRecipes();
     	
    }
    
    public void loadRecipes()
    {
    	/*
    	debug("Loading Grenades ...");
    	for (String mat : getConfig().getConfigurationSection("grenade").getKeys(false)) {
    		Material mt = Material.getMaterial(Integer.valueOf(mat));
    			ShapelessRecipe grenade = new ShapelessRecipe(new ItemStack(Material.EGG));
    	   	 	grenade.addIngredient(Material.EGG,-1);
    	   	 	grenade.addIngredient(mt,-1);
    	   	 	this.getServer().addRecipe(grenade);
		}

    	debug("Loading Wands ...");    	
    	for (String mat : getConfig().getConfigurationSection("wand").getKeys(false)) {
    		Material mt = Material.getMaterial(Integer.valueOf(mat));
    	   	 	ShapelessRecipe rod = new ShapelessRecipe(new ItemStack(Material.BLAZE_ROD));
    	   	 	rod.addIngredient(Material.BLAZE_ROD,-1);
    	   	 	rod.addIngredient(mt,-1);
    	   	 	this.getServer().addRecipe(rod);
    	}
	*/
    	debug("Loading Potions ...");
      	for (String mat : getConfig().getConfigurationSection("potion").getKeys(false)) {
      		if ( mat.equalsIgnoreCase("allowWater") || mat.equalsIgnoreCase("allowSplash") )
      			continue;
    		Material mt = Material.getMaterial(Integer.valueOf(mat));
  			ShapelessRecipe pot = new ShapelessRecipe(new ItemStack(Material.POTION));
    	   	 	pot.addIngredient(Material.POTION,-1);
    	   	 	pot.addIngredient(mt,-1);
    	   	 	this.getServer().addRecipe(pot);
      	}
    }
    
    public void setConfig(String line, Object set )
    {
    	if ( !getConfig().contains(line) ) getConfig().set(line,set);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("alchemist") ) {
    		
    		if ( args.length == 0 || ( args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("version") )) )
    		{
    			msg(sender,"Alchemist - by raum266 - version "+plugin.getDescription().getVersion());
				return true;
    		}

    		int num = 1;
    		ArrayList<String> a = new ArrayList<String>();
    		
    		while ( num < args.length )
    		{
    			a.add( args[num]);
    			num++;
    		}
    	
    		switch ( args[0].toLowerCase() )
    		{
    		default: sender.sendMessage("That is an invalid Alchemist command."); break;
    		case "reload":
    			if ( !sender.hasPermission("alchemist.admin") )
    			{
    				sender.sendMessage("I'm sorry, Dave, I can't let you do that.");
    				return true;
    			}
    			reloadConfig();
    			loadConfig();
    			saveConfig();
    			sender.sendMessage("Alchemist reloaded. Note: Recipes will not be removed until the next reboot.");
    			break;
    		case "potion": 
    			if ( !sender.hasPermission("alchemist.admin") )
    			{
    				sender.sendMessage("I'm sorry, Dave, I can't let you do that.");
    				return true;
    			}
    			commandPotion( sender, a); break;
    			
    		}
    		
    		return true;
    	}
    		return false;
    	
    	}
    void commandPotion( CommandSender s, ArrayList<String> a ) {
    	Integer item = 0;

		if ( a.size() <= 1 )
		{
			s.sendMessage("Valid potion options are: info (item), new (item), edit (item) (options), delete (item)");
			return;
		}
		
		try { 
	        Integer.parseInt( a.get(1) ); 
	        item = Integer.valueOf( a.get(1) );
	    } catch(NumberFormatException e) { 
	        item = 0; 
	    }

		if (a.get(0).equalsIgnoreCase("new") )
		{
			if ( getConfig().contains("potion."+item+".power") ) {
				s.sendMessage("That item already exists in the Alchemist database.");
				return;
			}			
	    	setConfig("potion."+item+".effect","FAST_DIGGING");
	    	setConfig("potion."+item+".duration",60);
	    	setConfig("potion."+item+".power",1);
	    	setConfig("potion."+item+".requires",0);
	    	setConfig("potion."+item+".overwrite",true);
	    	setConfig("potion."+item+".name","UnNamed Potion");
	    	
	    	s.sendMessage("Item added to Alchemist Potions. Use /alch edit "+ item + " to continue.");
	    	
	   
	    		Material mt = Material.getMaterial(Integer.valueOf(item));
	  			ShapelessRecipe pot = new ShapelessRecipe(new ItemStack(Material.POTION));
	    	   	 	pot.addIngredient(Material.POTION,-1);
	    	   	 	pot.addIngredient(mt,-1);
	    	   	 	this.getServer().addRecipe(pot);
	      	
	    	
	    	saveConfig();
	    	return;
		}
		else if ( a.get(0).equalsIgnoreCase("info") || a.get(0).equalsIgnoreCase("view") )
		{
			s.sendMessage("Viewing item " + item + ": " + Material.getMaterial(item) );
			s.sendMessage("Effect: " + getConfig().getString("potion."+item+".effect") );
			s.sendMessage("Duration: " + getConfig().getInt("potion."+item+".duration") );
			s.sendMessage("Power: " + getConfig().getInt("potion."+item+".power") );
			s.sendMessage("Requires: " + getConfig().getInt("potion."+item+".requires") );
			s.sendMessage("Overwrite: " + getConfig().getBoolean("potion."+item+".") );
			s.sendMessage("Name: " + getConfig().getString("potion."+item+".name") );
			s.sendMessage("Use /alch edit "+item+" (option) to configure.");
			return;
		}
		
		else if (a.get(0).equalsIgnoreCase("edit") )
		{
			if ( !(getConfig().contains("potion."+item+".power") )) {
				s.sendMessage("That item does not exist in the database.");
				return;
			}			
			
			debug(a.toString());
			
			if ( a.size() <= 2 ) { s.sendMessage("Syntax: /alch potion edit (item) (option) (setting)"); return; }
			
			if (a.get(2).equalsIgnoreCase("effect")) {
				PotionEffectType pt = PotionEffectType.getByName(a.get(3));
				if ( pt == null )
				{
					s.sendMessage("That is an invalid potion effect. Valid effects are:\n");
					StringBuilder sb = new StringBuilder();
					for (PotionEffectType pe : PotionEffectType.values()) {
						if ( pe != null && pe.getName() != null )
						{
					    if (sb.length() > 0) {
					        sb.append(", "); //separate values with a comma and space
					    }
					    sb.append(pe.getName());
						}
					}
					s.sendMessage( sb.toString() );
					return;
				}
					getConfig().set("potion."+item+".effect", pt.getName());
					s.sendMessage("Effect set.");
					
			} else if (a.get(2).equalsIgnoreCase("duration")) {
				Integer dur = getInt(a.get(3));
				if (dur == -1) {
					s.sendMessage("Invalid number.");
					return;
				}
				getConfig().set("potion."+item+".duration", dur);
				s.sendMessage("Duration set to "+dur);
						
			} else if (a.get(2).equalsIgnoreCase("power")) {
				
				Integer dur = getInt(a.get(3));
				if (dur == -1) {
					s.sendMessage("Invalid number.");
					return;
				}
				getConfig().set("potion."+item+".power", dur);
				s.sendMessage("Power set to "+dur);

			} else if (a.get(2).equalsIgnoreCase("requires")) {
				s.sendMessage("'Requires' is currently not enabled.");
				return;
				
			} else if (a.get(2).equalsIgnoreCase("overwrite")) {
					return;
			} else if (a.get(2).equalsIgnoreCase("name")) {
				getConfig().set("potion."+item+".name", a.get(3));
				s.sendMessage("Name set to "+ a.get(3));

			} else {
				s.sendMessage("That is not a valid option to edit.");
				return;
			}

			saveConfig();
			return;

		}
		else if (a.get(0).equalsIgnoreCase("delete") )
		{
			if ( !(getConfig().contains("potion."+item+".power") )) {
				s.sendMessage("That item does not exist in the database.");
				return;
			}
			
			getConfig().set("potion."+item, null);
			s.sendMessage("Item "+item+" removed from Alchemist database.");
			saveConfig();
			return;
		}
			
    }
    
    public Integer getInt( String a)
    {
    	int num = -1;
     	try {
       	 num = Integer.parseInt(a);
        	} catch (NumberFormatException e) {
       		return -1;
       	}
       	
       	return num;
    	
    }
    boolean addEffect( ItemStack orig, ItemStack item, int eff )
    {
    	List<String> l = new ArrayList<String>();
    	String effect = Material.getMaterial(eff).toString();
    	ItemMeta im = item.getItemMeta();
    	String type = "";
    	
    	if ( orig.getItemMeta().getLore() == null ) // New Eff
    	{
    		debug("New effects");
			l.add(ChatColor.AQUA + "Alchemist Effects:");

			if (orig.getType() == Material.POTION)
				im.setDisplayName("Potion of");
			if (orig.getType() == Material.EGG)
				im.setDisplayName("Grenade of");
			if (orig.getType() == Material.BLAZE_ROD)
				im.setDisplayName("Wand of");

    	}
    	else
    	{
    		debug("Existing Effects");
    		l = orig.getItemMeta().getLore();
    	}

		if (orig.getType() == Material.POTION)
			type = "potion.";
		if (orig.getType() == Material.EGG)
			type = "grenade.";
		if (orig.getType() == Material.BLAZE_ROD)
			type = "wand.";

		
    		if ( l.size() <= 1 )
    			im.setDisplayName(im.getDisplayName() + " " + getConfig().getString(type+eff+".name") );
    		else
    			im.setDisplayName(orig.getItemMeta().getDisplayName() + " and " + getConfig().getString(type +eff+".name") );
    	
    	l.add(ChatColor.RED + effect);
    	im.setLore(l);
    	item.setItemMeta(im);
    	

    	return true;
        
    }



    
    @EventHandler
    public void onSplash( PotionSplashEvent event )
    {
    	if ( getConfig().getBoolean("potion.allowSplash") == false )
    		return;
    	
    	if ( event.getPotion().getItem().getItemMeta().getLore() == null )
    		return;
    	
    	debug("Splash");
    	for ( LivingEntity a : event.getAffectedEntities() )
    	{
    		
    		addAff(a, event.getPotion().getItem().getItemMeta().getLore());
    		continue;
    	}
    }
    

    @EventHandler
    public void onCraftItem( CraftItemEvent event )
    {
    	
  	  if ( event.getRecipe().getResult().getType() == Material.POTION )
  	  {
  		  if ( !checkPot( event.getRecipe(), event.getInventory() ) )
  		  {
    			event.setCancelled(false);
    			Player p = (Player) event.getView().getPlayer();
    			p.sendMessage("There are too many alchemist items on that potion already!");
    			return;

  		  }
  				
  	  }
  	  
  	 if ( event.getRecipe().getResult().getType() == Material.EGG )
 	  {
 		  if ( !checkEgg( event.getRecipe(), event.getInventory() ) )
 		  {
   			event.setCancelled(false);
   			Player p = (Player) event.getView().getPlayer();
   			p.sendMessage("There are too many alchemist items on that grenade already!");
   			return;

 		  }
 				
 	  }
  	 
 	 if ( event.getRecipe().getResult().getType() == Material.BLAZE_ROD)
	  {
		  if ( !checkRod( event.getRecipe(), event.getInventory() ) )
		  {
  			event.setCancelled(false);
  			Player p = (Player) event.getView().getPlayer();
  			p.sendMessage("There are too many alchemist items on that rod already!");
  			return;

		  }
				
	  }
    }
    
    @EventHandler
    public void onCraftPrep( PrepareItemCraftEvent event )
    {

	  if ( event.getRecipe().getResult().getType() == Material.POTION )
    	  {
    		  if ( !checkPot( event.getRecipe(), event.getInventory() ) )
    		  {
      			Player p = (Player) event.getView().getPlayer();
      			p.sendMessage("There are too many alchemist items on that potion already!");
      			return;

    		  }
    				
    	  }
    	  
    	 if ( event.getRecipe().getResult().getType() == Material.EGG )
   	  {
   		  if ( !checkEgg( event.getRecipe(), event.getInventory() ) )
   		  {
     			Player p = (Player) event.getView().getPlayer();
     			p.sendMessage("There are too many alchemist items on that grenade already!");
     			return;

   		  }
   				
   	  }
    	 
   	 if ( event.getRecipe().getResult().getType() == Material.BLAZE_ROD)
  	  {
  		  if ( !checkRod( event.getRecipe(), event.getInventory() ) )
  		  {
    			Player p = (Player) event.getView().getPlayer();
    			p.sendMessage("There are too many alchemist items on that rod already!");
    			return;

  		  }
  				
  	  }
    }
    
    @EventHandler
    public void onPotion( PlayerItemConsumeEvent event )
    {

  	if ( event.getItem().getType() != Material.POTION )
    		return;
    	
        	if ( event.getItem().getItemMeta().getLore() == null )
        		return;
 	
    	addAff( (LivingEntity) event.getPlayer(), event.getItem().getItemMeta().getLore() );
    	
    }
    


 boolean checkPot( Recipe r, CraftingInventory i )
 {
	 int count = 0;
	 ItemStack pot = null;
	 ItemStack inv = null;
	 
	   for ( ItemStack a : i.getContents() )
       {
		   
		   if ( count == 0 ) { count++; continue; }

       if ( a.getType() == Material.AIR )
    	   continue;
       
       if ( a.getType() == Material.POTION )
       {
    	   pot = a;
    	   continue;
       }
       
       		inv = a;
       continue;
       
       }       		

	   if ( pot == null || inv == null )
	   {
		   debug("Potion or Inventory is null");
		   return true;
	   }

	   if ( pot.getDurability() == 0 && getConfig().getBoolean("potion.allowWater") == false )
	   {
		   debug("No water allowed.");
		   return true;
	   }
	   
	   
  		ItemStack res = new ItemStack( pot.getType() );

	   if ( pot.getDurability() != 0 )
	   {
       		Potion b = Potion.fromItemStack(pot);
    		b.apply(res);
	   }
       		
       		if ( !addEffect(pot, res,inv.getTypeId()) )
       		{
       			return false;
       		}

       		i.setResult(res);
       		return true;
       		
 }

 boolean checkRod( Recipe r, CraftingInventory i )
 {
	 int count = 0;
	 ItemStack pot = null;
	 ItemStack inv = null;
	 
	   for ( ItemStack a : i.getContents() )
       {
		   
		   if ( count == 0 ) { count++; continue; }

       if ( a.getType() == Material.AIR )
    	   continue;
       
       if ( a.getType() == Material.BLAZE_ROD )
       {
    	   pot = a;
    	   continue;
       }
       
       		inv = a;
       continue;
       
       }       		

	   if ( pot == null || inv == null )
	   {
		   debug("Rod or Inventory is null");
		   return true;
	   }


	   
  		ItemStack res = new ItemStack( pot.getType() );

       		if ( !addEffect(pot, res,inv.getTypeId()) )
       		{
       			return false;
       		}

       		i.setResult(res);
       		return true;
       		
 }
 
 boolean checkEgg( Recipe r, CraftingInventory i )
 {
	 int count = 0;
	 ItemStack pot = null;
	 ItemStack inv = null;
	 
	   for ( ItemStack a : i.getContents() )
       {
		   
		   if ( count == 0 ) { count++; continue; }

       if ( a.getType() == Material.AIR )
    	   continue;
       
       if ( a.getType() == Material.EGG )
       {
    	   pot = a;
    	   continue;
       }
       
       		inv = a;
       continue;
       
       }       		

	   if ( pot == null || inv == null )
	   {
		   debug("Egg or Inventory is null");
		   return true;
	   }

	   
  		ItemStack res = new ItemStack( pot.getType() );
	
       		if ( !addEffect(pot, res,inv.getTypeId()) )
       		{
       			return false;
       		}

       		i.setResult(res);
       		return true;
       		
 }
 
 void addAff(LivingEntity a, List<String> af )
 {
 	
 	int num = 0;
 	for ( String b : af )
 	{
 		if ( num == 0 ) { num++; continue; } 
 		
 		b = b.substring(2);
 			
     	debug("Adding Affect: "+ b + " to " + a.toString() );
     	Material mat = Material.getMaterial(b);
     	
     	int eff = mat.getId();
     	debug("Eff: "+eff+" - Mat: " + mat);
     	debug(getConfig().getString("potion."+eff+".effect"));

     	
     	PotionEffectType pt = PotionEffectType.getByName(getConfig().getString("potion."+eff+".effect"));
     	int power = getConfig().getInt("potion."+eff+".power");
     	
     	int duration = getConfig().getInt("potion."+eff+".duration") * 20;
     	
        a.addPotionEffect(new PotionEffect(pt,duration,power));
        	
 		continue;
 	}
 }

    
 }