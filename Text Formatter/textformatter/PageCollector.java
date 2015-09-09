package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Brian Truong.
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        _out = out;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        _out.add(line);
    }
    /** ArrayList that stores all the formulated and complete lines. */
    private List<String> _out;
}
