    package jari_.brostasticsmp.mixin;

    import com.mojang.authlib.GameProfile;
    import jari_.brostasticsmp.BrostasticSMP;
    import net.minecraft.server.*;
    import net.minecraft.server.network.ServerPlayerEntity;
    import net.minecraft.text.MutableText;
    import net.minecraft.text.Text;
    import org.spongepowered.asm.mixin.Mixin;
    import org.spongepowered.asm.mixin.Shadow;
    import org.spongepowered.asm.mixin.injection.At;
    import org.spongepowered.asm.mixin.injection.Inject;
    import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

    import java.net.SocketAddress;
    import java.text.SimpleDateFormat;
    import java.util.List;
    import java.util.Map;

    @Mixin(PlayerManager.class)
    public abstract class PlayerManagerMixin {

        @Shadow private BannedPlayerList bannedProfiles;
        @Shadow private BannedIpList bannedIps;
        @Shadow private List<ServerPlayerEntity> players;
        @Shadow private int maxPlayers;
        @Shadow protected abstract boolean isWhitelisted(GameProfile profile);
        @Shadow protected abstract boolean canBypassPlayerLimit(GameProfile profile);

        private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        @Inject(method = "checkCanJoin", at = @At("HEAD"), cancellable = true)
        public void checkCanJoin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
            MutableText mutableText;

            // Check if the player is banned
            if (this.bannedProfiles.contains(profile)) {
                BannedPlayerEntry bannedPlayerEntry = (BannedPlayerEntry) this.bannedProfiles.get(profile);
                mutableText = Text.literal("§4§l=== DU BIST GEBANNT ===\n\n");
                mutableText.append(Text.literal("§3Grund: "))
                        .append(Text.literal("§4" + bannedPlayerEntry.getReason()).append(Text.literal("\n\n§3Erstellt: §2" + DATE_FORMATTER.format(bannedPlayerEntry.getCreationDate()))));
                if (bannedPlayerEntry.getExpiryDate() != null) {
                    mutableText.append(Text.literal("\n\n§3Ablauf: "))
                            .append(Text.literal("§2" + DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
                } else {
                    mutableText.append(Text.literal("\n\n§c§lAblauf: §4§lNie"));
                }
                mutableText.append(Text.literal("\n\n§6Quelle: §2" + bannedPlayerEntry.getSource()));
                mutableText.append(Text.literal("\n\n§7Bei Fragen wende dich an unser Team."));
                cir.setReturnValue(mutableText);
                return;
            }

            // Check if the player is whitelisted
            if (!this.isWhitelisted(profile)) {
                mutableText = Text.literal("§c§l=== ZUGRIFF VERWEIGERT ===\n\n");
                mutableText.append(Text.literal("§e§lGrund:§r "))
                        .append(Text.literal("Du stehst nicht auf der Whitelist."))
                        .append(Text.literal("\n\n§7Melde dich bei unserem Team für Zugang."));
                cir.setReturnValue(mutableText);
                return;
            }

            // Check if the IP is banned
            if (this.bannedIps.isBanned(address)) {
                BannedIpEntry bannedIpEntry = this.bannedIps.get(address);
                mutableText = Text.literal("§c§l=== IP GEBANNT ===\n\n");
                mutableText.append(Text.literal("§e§lGrund:§r "))
                        .append(Text.literal(bannedIpEntry.getReason()))
                        .append(Text.literal("\n\n§e§lDauer:§r "));

                if (bannedIpEntry.getExpiryDate() != null) {
                    mutableText.append(Text.literal("Bis zum "))
                            .append(Text.literal(DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
                } else {
                    mutableText.append(Text.literal("§c§lPERMANENT"));
                }
                mutableText.append(Text.literal("\n\n§7Kontaktiere unser Team für Hilfe."));
                cir.setReturnValue(mutableText);
                return;
            }

            // Check if the server is full
            if (this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(profile)) {
                mutableText = Text.literal("§c§l=== SERVER VOLL ===\n\n");
                mutableText.append(Text.literal("§e§lInfo:§r "))
                        .append(Text.literal("Der Server hat seine maximale Spieleranzahl erreicht."))
                        .append(Text.literal("\n\n§7Versuche es später erneut."));
                cir.setReturnValue(mutableText);
                return;
            }

            //Check if the Serve ris closed.
            if(BrostasticSMP.gesperrt){
                mutableText = Text.literal("§c§l=== SERVER gesperrt ===\n\n");
                mutableText.append(Text.literal("§e§lInfo:§r "))
                        .append(Text.literal("Der Server ist acktuell gesperrt."))
                        .append(Text.literal("\n\n§7Versuche es später erneut."));
                cir.setReturnValue(mutableText);
                return;
            }

            // If everything is fine, just return null to allow the original method to proceed
            cir.setReturnValue(null);
        }
    }