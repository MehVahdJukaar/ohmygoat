package net.mehvahdjukaar.ohmygoat.common;

import com.google.gson.JsonParser;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynServerResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.ohmygoat.OhMyGoat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;

public class PackProvider extends DynServerResourcesProvider {

    public static final PackProvider INSTANCE = new PackProvider();

    public PackProvider() {
        super(new DynamicDataPack(OhMyGoat.res("generated_pack"), Pack.Position.BOTTOM, true, true));
        this.dynamicPack.generateDebugResources = false;
    }

    @Override
    public Logger getLogger() {
        return OhMyGoat.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return false;
    }

    @Override
    public void regenerateDynamicAssets(ResourceManager manager) {

        ResourceLocation res = new ResourceLocation("entities/goat");

        var json = JsonParser.parseString("""
                {
                  "type": "minecraft:entity",
                  "pools": [
                    {
                      "bonus_rolls": 0.0,
                      "entries": [
                        {
                          "type": "minecraft:item",
                          "functions": [
                            {
                              "add": false,
                              "count": {
                                "type": "minecraft:uniform",
                                "max": 2.0,
                                "min": 1.0
                              },
                              "function": "minecraft:set_count"
                            },
                            {
                              "conditions": [
                                {
                                  "condition": "minecraft:entity_properties",
                                  "entity": "this",
                                  "predicate": {
                                    "flags": {
                                      "is_on_fire": true
                                    }
                                  }
                                }
                              ],
                              "function": "minecraft:furnace_smelt"
                            },
                            {
                              "count": {
                                "type": "minecraft:uniform",
                                "max": 1.0,
                                "min": 0.0
                              },
                              "function": "minecraft:looting_enchant"
                            }
                          ],
                          "name": "ohmygoat:chevon"
                        }
                      ],
                      "rolls": 1.0
                    }
                  ]
                }""");
        if (!PlatformHelper.isModLoaded("windswept")) {
            dynamicPack.addJson(res, json, ResType.LOOT_TABLES);
        }
    }

    @Override
    public void generateStaticAssetsOnStartup(ResourceManager manager) {

    }


}
