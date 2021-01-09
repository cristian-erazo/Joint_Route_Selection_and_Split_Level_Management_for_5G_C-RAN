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
    prc 4
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
    ant 1
    prb 2
    x 65
    y 42
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 4
    prb 5
    x 68
    y 35
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 4
    prb 3
    x 75
    y 48
  ]
  node [
    id 6
    label "6"
    type 1
    prc 3
    ant 2
    prb 5
    x 18
    y 12
  ]
  edge [
    source 0
    target 4
    bandwith 786
    delay 415
  ]
  edge [
    source 1
    target 5
    bandwith 856
    delay 413
  ]
  edge [
    source 2
    target 3
    bandwith 106
    delay 443
  ]
  edge [
    source 2
    target 6
    bandwith 421
    delay 409
  ]
]
