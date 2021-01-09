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
    prc 3
    ant 5
    prb 3
    x 66
    y 40
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 2
    prb 6
    x 44
    y 77
  ]
  edge [
    source 0
    target 3
    bandwith 423
    delay 409
  ]
  edge [
    source 1
    target 2
    bandwith 902
    delay 472
  ]
]
