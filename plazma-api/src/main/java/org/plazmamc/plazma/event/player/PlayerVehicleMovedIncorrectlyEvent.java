package org.plazmamc.plazma.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * Runs when the player's vehicle moves incorrectly. This is similar to
 * {@link io.papermc.paper.event.player.PlayerFailMoveEvent}, but for vehicle.
 *
 * @see io.papermc.paper.event.player.PlayerFailMoveEvent
 */
public class PlayerVehicleMovedIncorrectlyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Vehicle vehicle;
    private final Location from;
    private final Location to;
    private final Reason reason;
    private boolean isCancelled;
    private boolean logWarnings;

    @ApiStatus.Internal
    public PlayerVehicleMovedIncorrectlyEvent(final @NonNull Player who, final @NonNull Vehicle vehicle, final @NonNull Location from, final @NonNull Location to, final @NonNull Reason reason, final boolean isCancelled) {
        super(who);
        this.vehicle = vehicle;
        this.from = from;
        this.to = to;
        this.reason = reason;
        this.isCancelled = isCancelled;
        this.logWarnings = true;
    }

    /**
     * Gets the vehicle of the player
     *
     * @return The vehicle of the player
     */
    public @NonNull Vehicle getVehicle() {
        return this.vehicle;
    }

    /**
     * Gets the location this player moved from
     *
     * @return Location the player moved from
     */
    public @NonNull Location getFrom() {
        return this.from.clone();
    }

    /**
     * Gets the location this player moved to
     *
     * @return Location the player moved to
     */
    public @NonNull Location getTo() {
        return this.to.clone();
    }

    /**
     * Gets the reason this event fired
     *
     * @return The reason this event fired
     */
    public @NonNull Reason getReason() {
        return this.reason;
    }

    /**
     * Gets whether the server should log warnings for this event, for example,
     * {@code "Oak boat (vehicle of ThePlayer) moved too quickly!}
     *
     * @return Whether to log warnings
     */
    public boolean shouldLogWarnings() {
        return this.logWarnings;
    }

    /**
     * Sets whether the server should log warnings for this event, for example,
     * {@code "Oak boat (vehicle of ThePlayer) moved too quickly!}
     *
     * @param logWarnings Whether to log warnings
     */
    public void shouldLogWarnings(final boolean logWarnings) {
        this.logWarnings = logWarnings;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum Reason {
        MOVED_TOO_QUICKLY,
        MOVED_WRONGLY
    }

}
