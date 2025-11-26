import java.util.concurrent.atomic.AtomicReference;

public class ABAProblem {
    private final AtomicReference<ValueWrapper> sharedValue;
    
    public ABAProblem() {
        this.sharedValue = new AtomicReference<>(new ValueWrapper("A", 1));
    }
    
    public ValueWrapper getCurrentValue() {
        return sharedValue.get();
    }
    
    public boolean attemptUpdate(ValueWrapper expectedValue, ValueWrapper newValue) {
        return sharedValue.compareAndSet(expectedValue, newValue);
    }
    
    public void performABAChange() {
        ValueWrapper current = sharedValue.get();
        sharedValue.set(new ValueWrapper("B", current.version + 1));
        sharedValue.set(new ValueWrapper("A", current.version + 2));
    }
    
    public static class ValueWrapper {
        final String value;
        final int version;
        
        public ValueWrapper(String value, int version) {
            this.value = value;
            this.version = version;
        }
        
        @Override
        public String toString() {
            return value + " (v" + version + ")";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ValueWrapper that = (ValueWrapper) obj;
            return value.equals(that.value);
        }
    }
}

