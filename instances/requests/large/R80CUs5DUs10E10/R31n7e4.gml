graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 8
    prb 5
    x 23
    y 42
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 7
    prb 3
    x 20
    y 74
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 2
    prb 4
    x 42
    y 74
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 10
    prb 6
    x 65
    y 92
  ]
  edge [
    source 0
    target 4
    bandwith 715
    delay 170
  ]
  edge [
    source 1
    target 6
    bandwith 685
    delay 194
  ]
  edge [
    source 2
    target 3
    bandwith 535
    delay 134
  ]
  edge [
    source 2
    target 5
    bandwith 822
    delay 447
  ]
]
