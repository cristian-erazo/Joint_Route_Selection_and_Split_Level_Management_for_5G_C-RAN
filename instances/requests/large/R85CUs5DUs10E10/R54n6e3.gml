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
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 3
    prb 2
    x 107
    y 60
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 9
    prb 3
    x 39
    y 41
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 3
    prb 5
    x 87
    y 93
  ]
  edge [
    source 0
    target 5
    bandwith 105
    delay 268
  ]
  edge [
    source 1
    target 3
    bandwith 557
    delay 279
  ]
  edge [
    source 2
    target 4
    bandwith 116
    delay 236
  ]
]
