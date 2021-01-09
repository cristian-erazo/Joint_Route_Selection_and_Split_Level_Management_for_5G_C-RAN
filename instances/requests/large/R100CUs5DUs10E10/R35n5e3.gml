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
    type 1
    prc 2
    ant 2
    prb 3
    x 101
    y 39
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 5
    prb 2
    x 53
    y 35
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 8
    prb 4
    x 56
    y 82
  ]
  edge [
    source 0
    target 2
    bandwith 226
    delay 363
  ]
  edge [
    source 1
    target 4
    bandwith 170
    delay 448
  ]
  edge [
    source 1
    target 3
    bandwith 246
    delay 119
  ]
]
