package sonnicon.venture.util;

import io.anuke.arc.func.Boolf;
import io.anuke.arc.math.Mathf;
import io.anuke.mindustry.entities.type.Unit;

public class UnitsUtil{
    private static Unit result;
    private static float cdist;

    public static Unit closest(float x, float y, float width, float height, Boolf<Unit> predicate) {
        result = null;
        cdist = 0.0F;
        io.anuke.mindustry.entities.Units.nearby(x, y, width, height, (e) -> {
            if (predicate.get(e)) {
                float dist = Mathf.dst2(e.x, e.y, x, y);
                if (result == null || dist < cdist) {
                    result = e;
                    cdist = dist;
                }
            }
        });
        return result;
    }

    public static Unit closest(float x, float y, float radius, Boolf<Unit> predicate) {
        return closest(x - radius, y - radius, radius * 2f, radius * 2f, predicate);
    }
}
