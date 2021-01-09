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
    type 1
    prc 4
    ant 10
    prb 5
    x 57
    y 70
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 2
    prb 2
    x 23
    y 11
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 6
    prb 2
    x 34
    y 106
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 8
    prb 6
    x 78
    y 67
  ]
  edge [
    source 0
    target 2
    bandwith 859
    delay 441
  ]
  edge [
    source 0
    target 4
    bandwith 170
    delay 448
  ]
  edge [
    source 1
    target 3
    bandwith 748
    delay 458
  ]
  edge [
    source 1
    target 5
    bandwith 313
    delay 121
  ]
]
