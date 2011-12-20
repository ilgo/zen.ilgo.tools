package zen.ilgo.star;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {DictFileTest.class,
         SynFileTest.class,
         IdxFileTest.class,
         IfoFileTest.class,
         StarFactoryTest.class,
         StardictTest.class
         })

public class StarDictTestSuite  {}