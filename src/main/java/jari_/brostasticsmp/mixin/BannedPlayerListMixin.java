package jari_.brostasticsmp.mixin;

import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.SimpleDateFormat;

@Mixin(BannedPlayerList.class)
public class BannedPlayerListMixin {

    private static void onCreateBanReason(BannedPlayerEntry entry, CallbackInfoReturnable<Text> cir) {
        if (entry == null) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        StringBuilder reason = new StringBuilder();

        // Überschrift in Rot
        reason.append("§cDu wurdest gebannt!\n");

        // Grund
        reason.append("Reason: ").append(entry.getReason()).append("\n");

        // Ablaufdatum
        reason.append("Expires: ");
        if (entry.getExpiryDate() != null) {
            reason.append(dateFormat.format(entry.getExpiryDate()));
        } else {
            reason.append("PERMANENT");
        }

        cir.setReturnValue(Text.literal(reason.toString()));
    }
}