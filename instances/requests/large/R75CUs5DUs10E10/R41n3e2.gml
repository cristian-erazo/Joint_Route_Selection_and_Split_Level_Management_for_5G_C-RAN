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
    type 1
    prc 3
    ant 2
    prb 5
    x 42
    y 88
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 2
    prb 3
    x 58
    y 86
  ]
  edge [
    source 0
    target 2
    bandwith 108
    delay 170
  ]
  edge [
    source 0
    target 1
    bandwith 396
    delay 389
  ]
]
