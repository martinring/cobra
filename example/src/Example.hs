module Example where

--- begin #person-def-hs
data Person = Person Integer String
            | {-(-}Blub{-|Foo)-}
            | {-(-}Hallo{-)-}

f = 7 * 6
--- end #person-def-hs
