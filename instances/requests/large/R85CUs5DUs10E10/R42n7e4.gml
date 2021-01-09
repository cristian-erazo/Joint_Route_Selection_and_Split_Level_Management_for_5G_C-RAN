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
    prc 1
    ant 8
    prb 6
    x 32
    y 15
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 4
    prb 6
    x 93
    y 40
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 6
    prb 2
    x 56
    y 42
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 10
    prb 2
    x 73
    y 29
  ]
  edge [
    source 0
    target 6
    bandwith 314
    delay 386
  ]
  edge [
    source 1
    target 3
    bandwith 437
    delay 447
  ]
  edge [
    source 2
    target 5
    bandwith 765
    delay 276
  ]
  edge [
    source 2
    target 4
    bandwith 587
    delay 155
  ]
]
