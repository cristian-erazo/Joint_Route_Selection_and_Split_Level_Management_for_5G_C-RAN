graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 3
    x 40
    y 110
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 6
    prb 6
    x 14
    y 33
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 4
    prb 3
    x 30
    y 28
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 9
    prb 3
    x 101
    y 102
  ]
  edge [
    source 0
    target 3
    bandwith 974
    delay 146
  ]
  edge [
    source 1
    target 5
    bandwith 896
    delay 442
  ]
  edge [
    source 2
    target 4
    bandwith 996
    delay 154
  ]
  edge [
    source 2
    target 6
    bandwith 280
    delay 205
  ]
]
