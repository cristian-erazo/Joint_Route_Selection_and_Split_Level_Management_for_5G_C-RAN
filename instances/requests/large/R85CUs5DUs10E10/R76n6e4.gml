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
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 5
    prb 2
    x 33
    y 71
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 2
    prb 3
    x 60
    y 18
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 6
    prb 2
    x 42
    y 63
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 6
    prb 4
    x 25
    y 106
  ]
  edge [
    source 0
    target 3
    bandwith 558
    delay 128
  ]
  edge [
    source 1
    target 5
    bandwith 461
    delay 136
  ]
  edge [
    source 1
    target 4
    bandwith 808
    delay 402
  ]
  edge [
    source 1
    target 2
    bandwith 107
    delay 264
  ]
]
