package net.mehvahdjukaar.goated.common;

import com.google.gson.JsonParser;
import net.mehvahdjukaar.goated.Goated;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynServerResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;

public class PackProvider extends DynServerResourcesGenerator {

    public static final PackProvider INSTANCE = new PackProvider();

    public PackProvider() {
        super(new DynamicDataPack(Goated.res("generated_pack"), Pack.Position.BOTTOM, true, true));
        this.dynamicPack.setGenerateDebugResources(false);
        this.dynamicPack.addNamespaces("minecraft");
    }

    @Override
    public Logger getLogger() {
        return Goated.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return true;
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
                          "name": "goated:chevon"
                        }
                      ],
                      "rolls": 1.0
                    }
                  ]
                }""");
        var o = manager.getResource(ResType.LOOT_TABLES.getPath(res));
        try (var r = o.get().open()) {
            var j = RPUtils.deserializeJson(r);
            if (j.size() != 1) return;
        } catch (Exception ignored) {
        }
        if (!PlatHelper.isModLoaded("windswept")) {
            dynamicPack.addJson(res, json, ResType.LOOT_TABLES);
        }
    }

}
