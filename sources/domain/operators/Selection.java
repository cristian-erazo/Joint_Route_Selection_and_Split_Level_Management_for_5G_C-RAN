package domain.operators;

import domain.Algorithm;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public interface Selection<T> extends Algorithm {

    @Override
    public List<T> run();

    public void setSelectionSize(int size);

    public int getSelectionSize();

    public void setList(List<T> list);
}
