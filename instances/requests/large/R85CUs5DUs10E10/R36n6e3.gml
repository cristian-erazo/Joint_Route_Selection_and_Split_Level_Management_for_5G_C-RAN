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
    prc 2
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
    ant 2
    prb 4
    x 89
    y 44
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 8
    prb 2
    x 102
    y 36
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 9
    prb 5
    x 77
    y 99
  ]
  edge [
    source 0
    target 5
    bandwith 781
    delay 129
  ]
  edge [
    source 1
    target 3
    bandwith 441
    delay 354
  ]
  edge [
    source 2
    target 4
    bandwith 246
    delay 270
  ]
]
