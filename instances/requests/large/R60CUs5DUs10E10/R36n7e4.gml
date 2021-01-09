graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
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
    ant 5
    prb 5
    x 54
    y 82
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 8
    prb 2
    x 92
    y 54
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 7
    prb 6
    x 108
    y 75
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 8
    prb 6
    x 93
    y 22
  ]
  edge [
    source 0
    target 4
    bandwith 903
    delay 335
  ]
  edge [
    source 1
    target 5
    bandwith 388
    delay 274
  ]
  edge [
    source 2
    target 3
    bandwith 659
    delay 385
  ]
  edge [
    source 2
    target 6
    bandwith 321
    delay 204
  ]
]
