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
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 3
    prb 6
    x 39
    y 119
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 5
    prb 3
    x 100
    y 49
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 7
    prb 4
    x 41
    y 77
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 4
    prb 2
    x 85
    y 36
  ]
  edge [
    source 0
    target 4
    bandwith 320
    delay 324
  ]
  edge [
    source 1
    target 6
    bandwith 307
    delay 304
  ]
  edge [
    source 2
    target 3
    bandwith 994
    delay 274
  ]
  edge [
    source 2
    target 5
    bandwith 869
    delay 377
  ]
]
