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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 7
    prb 5
    x 45
    y 37
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 10
    prb 5
    x 50
    y 83
  ]
  edge [
    source 0
    target 3
    bandwith 845
    delay 295
  ]
  edge [
    source 1
    target 2
    bandwith 560
    delay 445
  ]
]
