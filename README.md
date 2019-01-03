Use classes from `common` package to describe node type, label and geometrical position.

```Java
// src/main/java/common/Type
public enum Type {
    VERTEX, HYPEREDGE
}
```

```Java
// src/main/java/common/Label
public enum Label {
    I, B, FW, FS, FE, FN, V
}
```

```Java
// src/main/java/common/Geom
public class Geom {
    private int x;
    private int y;

    public Geom(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
```