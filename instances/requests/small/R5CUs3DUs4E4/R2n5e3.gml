graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 1
    prb 4
    x 72
    y 62
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 2
    prb 5
    x 16
    y 17
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 2
    prb 5
    x 38
    y 81
  ]
  edge [
    source 0
    target 3
    bandwith 685
    delay 281
  ]
  edge [
    source 1
    target 4
    bandwith 887
    delay 251
  ]
  edge [
    source 1
    target 2
    bandwith 579
    delay 458
  ]
]
