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
    type 1
    prc 4
    ant 2
    prb 6
    x 56
    y 28
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 2
    prb 2
    x 25
    y 52
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 7
    prb 3
    x 35
    y 86
  ]
  edge [
    source 0
    target 2
    bandwith 722
    delay 321
  ]
  edge [
    source 0
    target 1
    bandwith 948
    delay 423
  ]
  edge [
    source 0
    target 3
    bandwith 986
    delay 140
  ]
]
