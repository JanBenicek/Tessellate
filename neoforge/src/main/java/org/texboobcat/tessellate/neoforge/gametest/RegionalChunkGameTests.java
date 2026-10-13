package org.texboobcat.tessellate.neoforge.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.texboobcat.tessellate.gametest.RegionalChunkGameTestCases;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Consumer;

import java.util.UUID;

public final class RegionalChunkGameTests {

    private static final DeferredRegister<Consumer<GameTestHelper>> FUNCTIONS =
        DeferredRegister.create(Registries.TEST_FUNCTION, "tessellate");

    public static void register(IEventBus bus) {
        FUNCTIONS.register("loadedunownedredstoneupdates", () -> RegionalChunkGameTests::loadedUnownedRedstoneUpdates);
        FUNCTIONS.register("torchburnoutstayslocal", () -> RegionalChunkGameTests::torchBurnoutStaysLocal);
        FUNCTIONS.register("unloadedredstoneupdates", () -> RegionalChunkGameTests::unloadedRedstoneUpdates);
        FUNCTIONS.register("redstonecircuitsandconcurrentsignals", () -> RegionalChunkGameTests::redstoneCircuitsAndConcurrentSignals);
        FUNCTIONS.register("mainthreadboundaryhandoffs", () -> RegionalChunkGameTests::mainThreadBoundaryHandoffs);
        FUNCTIONS.register("compatibilityapirunsonliveowners", () -> RegionalChunkGameTests::compatibilityApiRunsOnLiveOwners);
        FUNCTIONS.register("regionalchunkticksandnaturalspawning", () -> RegionalChunkGameTests::regionalChunkTicksAndNaturalSpawning);
        FUNCTIONS.register("pathfindingisconcurrentonregionworkers", () -> RegionalChunkGameTests::pathfindingIsConcurrentOnRegionWorkers);
        FUNCTIONS.register("mobaiticksonregionworkers", () -> RegionalChunkGameTests::mobAiTicksOnRegionWorkers);
        FUNCTIONS.register("scheduledtickrouterpreservesvanillacontainersemantics", () -> RegionalChunkGameTests::scheduledTickRouterPreservesVanillaContainerSemantics);
        FUNCTIONS.register("blockeventrouterpreservesorderdedupandmainthreadpackets", () -> RegionalChunkGameTests::blockEventRouterPreservesOrderDedupAndMainThreadPackets);
        FUNCTIONS.register(bus);
        bus.addListener(RegionalChunkGameTests::registerTests);
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        var environments = new java.util.HashMap<String,
            net.minecraft.core.Holder<net.minecraft.gametest.framework.TestEnvironmentDefinition<?>>>();
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "loadedunownedredstoneupdates");
            var environment = environments.computeIfAbsent("redstone-ownership",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 400, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "torchburnoutstayslocal");
            var environment = environments.computeIfAbsent("redstone-torches",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 400, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "unloadedredstoneupdates");
            var environment = environments.computeIfAbsent("redstone-boundary",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 200, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "redstonecircuitsandconcurrentsignals");
            var environment = environments.computeIfAbsent("redstone-circuits",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 400, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "mainthreadboundaryhandoffs");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 100, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "compatibilityapirunsonliveowners");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 200, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "regionalchunkticksandnaturalspawning");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 4800, 20, true,
                Rotation.NONE, false, 1, 1, true, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "pathfindingisconcurrentonregionworkers");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 200, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "mobaiticksonregionworkers");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 120, 20, true,
                Rotation.NONE, false, 1, 1, false, 16);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "scheduledtickrouterpreservesvanillacontainersemantics");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 100, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
        {
            Identifier id = Identifier.fromNamespaceAndPath("tessellate", "blockeventrouterpreservesorderdedupandmainthreadpackets");
            var environment = environments.computeIfAbsent("default",
                name -> event.registerEnvironment(Identifier.fromNamespaceAndPath("tessellate", name)));
            var data = new TestData<>(environment, Level.OVERWORLD,
                Identifier.withDefaultNamespace("bastion/blocks/air"), 100, 20, true,
                Rotation.NONE, false, 1, 1, false, 1);
            event.registerTest(id, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id), data));
        }
    }

    private RegionalChunkGameTests() {
    }

    public static void loadedUnownedRedstoneUpdates(GameTestHelper helper) {
        org.texboobcat.tessellate.gametest.RedstoneGameTestCases.loadedUnownedUpdates(helper);
    }

    public static void torchBurnoutStaysLocal(GameTestHelper helper) {
        org.texboobcat.tessellate.gametest.RedstoneGameTestCases.torchBurnoutStaysLocal(helper);
    }

    public static void unloadedRedstoneUpdates(GameTestHelper helper) {
        org.texboobcat.tessellate.gametest.RedstoneGameTestCases.unloadedUpdates(helper);
    }

    public static void redstoneCircuitsAndConcurrentSignals(GameTestHelper helper) {
        org.texboobcat.tessellate.gametest.RedstoneGameTestCases.circuitsAndConcurrentSignals(helper);
    }

    public static void mainThreadBoundaryHandoffs(GameTestHelper helper) {
        RegionalChunkGameTestCases.mainThreadBoundaryHandoffs(helper);
    }

    public static void compatibilityApiRunsOnLiveOwners(GameTestHelper helper) {
        RegionalChunkGameTestCases.compatibilityApiRunsOnLiveOwners(helper);
    }

    public static void regionalChunkTicksAndNaturalSpawning(GameTestHelper helper) {
        RegionalChunkGameTestCases.regionalChunkTicksAndNaturalSpawning(
            helper, RegionalChunkGameTests::makeNegotiatedMockPlayer);
    }

    public static void pathfindingIsConcurrentOnRegionWorkers(GameTestHelper helper) {
        RegionalChunkGameTestCases.pathfindingIsConcurrentOnRegionWorkers(helper);
    }

    public static void mobAiTicksOnRegionWorkers(GameTestHelper helper) {
        RegionalChunkGameTestCases.mobAiTicksOnRegionWorkers(helper);
    }

    public static void scheduledTickRouterPreservesVanillaContainerSemantics(
            GameTestHelper helper) {
        RegionalChunkGameTestCases.scheduledTickRouterPreservesVanillaContainerSemantics(helper);
    }

    public static void blockEventRouterPreservesOrderDedupAndMainThreadPackets(
            GameTestHelper helper) {
        RegionalChunkGameTestCases.blockEventRouterPreservesOrderDedupAndMainThreadPackets(helper);
    }

    private static ServerPlayer makeNegotiatedMockPlayer(ServerLevel level, String name) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(profile, false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, profile,
            ClientInformation.createDefault()) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        return player;
    }
}
