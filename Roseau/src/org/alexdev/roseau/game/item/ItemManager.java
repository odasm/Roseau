package org.alexdev.roseau.game.item;

import java.util.HashMap;
import java.util.Map;

import org.alexdev.roseau.Roseau;

public class ItemManager {

	private Map<Integer, ItemDefinition> definitions;

	public ItemManager() {
		this.definitions = new HashMap<Integer, ItemDefinition>();
	}

	public void load() {
		this.loadDefinitions();
	}
	
	private void loadDefinitions() {
		this.definitions = Roseau.getDataAccess().getItem().getDefinitions();
	}

	public ItemDefinition getDefinition(int definitionId) {
		return definitions.get(definitionId);
	}
	
}