graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 1
    prc 2
    ant 9
    prb 6
    x 99
    y 100
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 10
    prb 3
    x 77
    y 61
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 10
    prb 6
    x 48
    y 44
  ]
  edge [
    source 0
    target 1
    bandwith 804
    delay 192
  ]
  edge [
    source 0
    target 3
    bandwith 622
    delay 271
  ]
  edge [
    source 0
    target 2
    bandwith 260
    delay 422
  ]
]
