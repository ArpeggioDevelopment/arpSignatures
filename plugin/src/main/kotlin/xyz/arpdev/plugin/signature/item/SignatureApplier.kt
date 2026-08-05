package xyz.arpdev.plugin.signature.item

import com.google.inject.Singleton
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

@Singleton
class SignatureApplier {
    fun apply(
        item: ItemStack,
        component: Component,
    ) {
        val meta = item.itemMeta
        meta.customName(component)
        item.itemMeta = meta
    }
}
